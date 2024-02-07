package it.hurts.sskirillss.octolib.config.data;

import com.jayway.jsonpath.DocumentContext;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfigContext {
    private DocumentContext source;

    public String jsonString() {
        return source.jsonString();
    }

    public <T> T json() {
        return source.json();
    }

    public ConfigContext set(String path, Object newValue) {
        source.set(path, newValue);

        return this;
    }

    public <T> T get(String path) {
        return source.read(path);
    }

    public <T> T get(String path, Class<T> type) {
        return source.read(path, type);
    }
}