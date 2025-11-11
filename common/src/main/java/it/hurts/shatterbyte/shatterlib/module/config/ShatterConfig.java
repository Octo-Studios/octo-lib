package it.hurts.shatterbyte.shatterlib.module.config;

import de.marhali.json5.*;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.shatterlib.module.config.util.Json5Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.hurts.shatterbyte.shatterlib.ShatterLib.LOGGER;

public abstract class ShatterConfig {
    @Exclude
    private Json5Object defaultSchema;
    @Exclude
    private Json5Object currentSchema;

    public static final Json5 JSON5 = Json5.builder(builder -> builder
            .quoteless()
            .writeComments()
            .parseComments()
            .prettyPrinting()
            .build());

    public ShatterConfig() {

    }

    public Json5Object getCurrentSchema() {
        if (this.currentSchema == null) {
            this.updateSchemaCache();
        }

        return currentSchema;
    }

    public void updateSchemaCache() {
        this.currentSchema = Json5Utils.encode(this).getAsJson5Object();
    }

    public Json5Object getDefaultSchema() {
        Class<? extends ShatterConfig> clazz = this.getClass();

        if (defaultSchema == null) {
            try {
                Constructor<? extends ShatterConfig> ctor = clazz.getDeclaredConstructor();
                ctor.setAccessible(true);
                ShatterConfig defaultInstance = ctor.newInstance();
                Json5Element encoded = Json5Utils.encode(defaultInstance);
                if (!encoded.isJson5Object()) {
                    throw new IllegalStateException("Default schema encoded to non-object for " + clazz.getName());
                }
                defaultSchema = encoded.getAsJson5Object();
            } catch (Exception e) {
                throw new RuntimeException("Failed to create default schema for " + clazz.getName(), e);
            }
        }

        return defaultSchema;
    }

    /**
     * Lookup a Json5Element by a path string.
     * <p>
     * Supported syntax:
     * <br>- dot field access: "colorMap.test1"
     * <br>- array index: "colorList[0]"
     * <br>- quoted keys for map entries: map['a.b'] or map["a.b"]
     *
     * @return Optional Json5Element
     */
    public Optional<Json5Element> getElement(String path, Json5Element root) {
        if (path == null || path.isEmpty()) {
            return Optional.empty();
        }

        Json5Element current = root; // start at top-level object

        // token regex: group1 = single-quoted key, group2 = double-quoted key, group3 = index, group4 = simple key
        Pattern tokenPattern = Pattern.compile("\\['([^']+)']|\\[\"([^\"]+)\"]|\\[(\\d+)]|([^.\\[]+)");
        Matcher m = tokenPattern.matcher(path);

        while (m.find()) {
            String singleQuoted = m.group(1);
            String doubleQuoted = m.group(2);
            String index = m.group(3);
            String simpleKey = m.group(4);

            String key = singleQuoted != null ? singleQuoted
                    : doubleQuoted != null ? doubleQuoted
                    : simpleKey;

            if (index != null) {
                // array index access
                if (!current.isJson5Array()) {
                    return Optional.empty();
                }

                Json5Array arr = current.getAsJson5Array();
                int i = Integer.parseInt(index);

                if (i < 0 || i >= arr.size()) {
                    return Optional.empty();
                }

                current = arr.get(i);
            } else {
                // key access on object (map/object)
                if (!current.isJson5Object()) {
                    return Optional.empty();
                }

                Json5Object obj = current.getAsJson5Object();
                if (!obj.has(key)) {
                    return Optional.empty();
                }

                current = obj.get(key);
            }
        }

        return Optional.ofNullable(current);
    }

    /**
     * Gets an element by path from the default schema
     * @see #getElement(String, Json5Element)
     */
    public Optional<Json5Element> getDefaultElement(String path) {
        return this.getElement(path, this.getDefaultSchema());
    }

    public <T> Optional<T> getValue(String path, Type type, Json5Element root) {
        Optional<Json5Element> elemOpt = this.getElement(path, root);
        if (elemOpt.isEmpty()) return Optional.empty();
        try {
            Json5Element elem = elemOpt.get();
            T decoded = Json5Utils.decode(elem, type);
            return Optional.ofNullable(decoded);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public <T> Optional<T> getDefaultValue(String path, Type type) {
        return this.getValue(path, type, this.getDefaultSchema());
    }

    public void save(Path configDir) {
        this.updateSchemaCache();

        Path configFile = configDir.resolve(this.getFileName());
        try {
            Json5Object configJson = this.getCurrentSchema();

            if (!this.getComment().isEmpty()) {
                configJson.setComment(this.getComment());
            }

            //Json5Object defaultSchema = this.getDefaultSchema();
            //Json5Utils.injectDefaultComments(configJson, defaultSchema);

            String jsonString = JSON5.serialize(configJson);
            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, jsonString, StandardCharsets.UTF_8);

        } catch (Exception e) {
            LOGGER.error("Failed to save config: {}", this.getFileName(), e);
        }
    }

    public void load(Path configDir) {
        this.load(configDir, true);
    }

    public void load(Path configDir, boolean withSave) {
        Path configFile = configDir.resolve(this.getFileName());

        if (!Files.exists(configFile)) {
            if (withSave) {
                LOGGER.info("Config file not found, creating default: {}", this.getFileName());
                this.save(configDir);
            }

            return;
        }

        try {
            String jsonString = Files.readString(configFile, StandardCharsets.UTF_8);
            Json5Element parsedElement = JSON5.parse(jsonString);

            if (!(parsedElement instanceof Json5Object)) {
                if (withSave) {
                    LOGGER.warn("Config file is not a JSON object, resetting: {}", this.getFileName());
                    this.save(configDir);
                }
                return;
            }

            Json5Object configJson = parsedElement.getAsJson5Object();
            int loadedSchemaVersion = configJson.get("schemaVersion") == null ? this.getSchemaVersion() : configJson.get("schemaVersion").getAsInt();
            int currentSchemaVersion = this.getSchemaVersion();

            while (loadedSchemaVersion < currentSchemaVersion) {
                SchemaFixer fixer = ConfigManager.getFixer(this.getClass(), loadedSchemaVersion);
                if (fixer == null) {
                    loadedSchemaVersion++;
                    continue;
                }

                loadedSchemaVersion = fixer.apply(configJson);
                LOGGER.info("Applied a schema fixer for {}, from version {} to version {}", this.getPath(), fixer.from(), fixer.to());
            }

            this.loadFromJson(configJson);
        } catch (Exception e) {
            LOGGER.error("Failed to load config: {}, using defaults.", this.getFileName(), e);
        }

        if (withSave) {
            this.save(configDir);
        } else {
            this.updateSchemaCache();
        }
    }

    public void loadFromJson(Json5Object root) {
        Json5Utils.deserializeObject(root, this);
    }

    public void loadFromJson(String json) {
        this.loadFromJson(JSON5.parse(json).getAsJson5Object());
    }

    public final String getFileName() {
        return this.getPath() + ".json5";
    }

    public final String getPath() {
        String suffix = "-" + this.getSide().name().toLowerCase();
        return this.getName() + suffix;
    }

    public abstract String getName();

    public String getComment() {
        return "";
    }

    public ConfigSide getSide() {
        return ConfigSide.COMMON;
    }

    public final boolean isClient() {
        return this.getSide() == ConfigSide.CLIENT;
    }

    public final boolean isCommon() {
        return this.getSide() == ConfigSide.COMMON;
    }

    public final boolean isServer() {
        return this.getSide() == ConfigSide.SERVER;
    }

    public abstract int getSchemaVersion();
}
