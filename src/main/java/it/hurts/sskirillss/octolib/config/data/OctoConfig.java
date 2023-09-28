package it.hurts.sskirillss.octolib.config.data;

import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.jayway.jsonpath.JsonPath;
import it.hurts.sskirillss.octolib.config.api.IOctoConfig;
import it.hurts.sskirillss.octolib.config.api.events.ConfigLoadEvent;
import it.hurts.sskirillss.octolib.config.api.events.ConfigSaveEvent;
import it.hurts.sskirillss.octolib.config.storage.ConfigStorage;
import it.hurts.sskirillss.octolib.config.utils.ConfigUtils;
import lombok.Data;
import lombok.SneakyThrows;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;

import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class OctoConfig {
    private final IOctoConfig constructor;

    private ConfigContext schema;
    private ConfigContext data;

    public OctoConfig(IOctoConfig constructor) {
        this.constructor = constructor;
    }

    @SneakyThrows
    public final OctoConfig saveToFile() {
        Path path = constructor.getPath();
        File file = path.toFile().getCanonicalFile().getParentFile();

        if (file.isDirectory() || file.mkdirs()) {
            ConfigSaveEvent event = new ConfigSaveEvent(this);

            ModLoader.get().postEventWrapContainerInModOrder(event);

            if (!event.isCanceled()) {
                try (Writer writer = Files.newBufferedWriter(path)) {
                    ConfigUtils.SERIALIZER.toJson(ConfigUtils.SERIALIZER.fromJson(event.getConfig().getData().jsonString(), JsonObject.class), writer);

                    writer.flush();
                }
            }
        }

        return this;
    }

    @SneakyThrows
    public final OctoConfig loadFromFile() {
        if (!Files.exists(constructor.getPath()))
            saveToFile();

        ConfigLoadEvent event = new ConfigLoadEvent(this, new ConfigContext(JsonPath.parse(constructor.getPath().toFile())));

        ModLoader.get().postEventWrapContainerInModOrder(event);

        if (!event.isCanceled())
            this.setData(event.getContext());

        return this;
    }

    public final OctoConfig reload() {
        this.loadFromFile();

        ConfigStorage.set(this);

        return this;
    }

    public final OctoConfig getOrSetup() {
        OctoConfig config = ConfigStorage.get(constructor);

        return config == null ? constructor.setup() : config;
    }

    public <T> T get(String path) {
        T value = data.get(path);
        T defaultValue = schema.get(path);

        return (value == null || value instanceof JsonNull) ? (defaultValue == null || defaultValue instanceof JsonNull) ? null : defaultValue : value;
    }

    public <T> T get(String path, Class<T> type) {
        T value = data.get(path, type);
        T defaultValue = schema.get(path, type);

        return (value == null || value instanceof JsonNull) ? (defaultValue == null || defaultValue instanceof JsonNull) ? null : defaultValue : value;
    }

    public String getString(String path) {
        return get(path, String.class);
    }

    public Double getDouble(String path) {
        return get(path, Double.class);
    }

    public Float getFloat(String path) {
        return get(path, Float.class);
    }

    public int getInt(String path) {
        return get(path, Integer.class);
    }

    public boolean getBoolean(String path) {
        return get(path, Boolean.class);
    }

    public short getShort(String path) {
        return get(path, Short.class);
    }

    public byte getByte(String path) {
        return get(path, Byte.class);
    }

    public long getLong(String path) {
        return get(path, Long.class);
    }
}