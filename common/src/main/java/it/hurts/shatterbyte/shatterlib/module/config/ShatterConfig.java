package it.hurts.shatterbyte.shatterlib.module.config;

import de.marhali.json5.*;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static it.hurts.shatterbyte.shatterlib.ShatterLib.LOGGER;

public abstract class ShatterConfig {
    public static final Json5 JSON5 = Json5.builder(builder -> builder
            .quoteless()
            .writeComments()
            .parseComments()
            .prettyPrinting()
            .build());

    public void save(Path configDir) {
        Path configFile = configDir.resolve(this.getPath() + ".json5");
        try {
            Json5Element configJson = Json5Utils.encode(this);

            String jsonString = JSON5.serialize(configJson);
            Files.createDirectories(configFile.getParent());
            Files.writeString(configFile, jsonString, StandardCharsets.UTF_8);

        } catch (Exception e) {
            LOGGER.error("Failed to save config: {}", this.getPath(), e);
        }
    }

    public void load(Path configDir) {
        Path configFile = configDir.resolve(this.getPath() + ".json5");

        if (!Files.exists(configFile)) {
            LOGGER.info("Config file not found, creating default: {}", this.getPath());
            this.save(configDir);
            return;
        }

        try {
            String jsonString = Files.readString(configFile, StandardCharsets.UTF_8);
            Json5Element parsedElement = JSON5.parse(jsonString);

            if (!(parsedElement instanceof Json5Object)) {
                LOGGER.warn("Config file is not a JSON object, resetting: {}", this.getPath());
                this.save(configDir);
                return;
            }
            Json5Object configJson = parsedElement.getAsJson5Object();

            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Exclude.class)) {
                    continue;
                }

                int mods = field.getModifiers();
                if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) {
                    continue;
                }

                String fieldName = field.getName();

                if (!configJson.has(fieldName)) {
                    continue;
                }

                try {
                    field.setAccessible(true);
                    Json5Element fieldElement = configJson.get(fieldName);
                    Type type = field.getGenericType();
                    field.set(this, Json5Utils.decode(fieldElement, type));
                } catch (Exception e) {
                    LOGGER.warn("Failed to load config entry '{}' in {}, using default.", fieldName, this.getPath(), e);
                }
            }

            this.save(configDir);
        } catch (Exception e) {
            LOGGER.error("Failed to load config: {}, using defaults.", this.getPath(), e);
            this.save(configDir);
        }
    }

    public abstract String getPath();
}
