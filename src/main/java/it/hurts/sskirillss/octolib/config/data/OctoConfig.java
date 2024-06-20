package it.hurts.sskirillss.octolib.config.data;

import com.google.gson.JsonElement;
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
import net.neoforged.neoforge.common.NeoForge;

import java.io.File;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

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

            NeoForge.EVENT_BUS.post(event);

            if (!event.isCanceled()) {
                try (Writer writer = Files.newBufferedWriter(path)) {
                    ConfigUtils.SERIALIZER.toJson(data.json(), writer);

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

        NeoForge.EVENT_BUS.post(event);

        if (!event.isCanceled()) {
            JsonElement data = event.getData().json();

            JsonElement result = invalidateJson(event.getConfig().getSchema().json(), data);

            this.setData(new ConfigContext(JsonPath.parse(result)));

            if (!data.equals(result))
                saveToFile();
        }

        ConfigStorage.set(this);

        return this;
    }

    protected final JsonElement invalidateJson(JsonElement original, JsonElement edited) throws StackOverflowError {
        if (!original.isJsonObject() || !edited.isJsonObject())
            return edited;

        JsonObject result = new JsonObject();

        JsonObject originalObj = original.getAsJsonObject();
        JsonObject editedObj = edited.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : originalObj.entrySet()) {
            String key = entry.getKey();
            JsonElement value = entry.getValue();

            if (editedObj.has(key)) {
                JsonElement editedValue = editedObj.get(key);

                if (value.isJsonObject() && editedValue.isJsonObject())
                    result.add(key, invalidateJson(value.getAsJsonObject(), editedValue.getAsJsonObject()));
                else
                    result.add(key, editedValue);
            } else
                result.add(key, value);
        }

        for (String key : editedObj.keySet()) {
            if (!originalObj.has(key))
                continue;

            if (!result.has(key))
                result.add(key, editedObj.get(key));
        }

        return result;
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