package it.hurts.shatterbyte.shatterlib.module.config.type;

import java.util.HashMap;
import java.util.Map;

public class MapEntry<V> extends AbstractEntry<HashMap<String, V>, MapEntry<V>> {
    private MapEntry(HashMap<String, V> defaultValue) {
        super(defaultValue);
    }

    public static class Builder<V> extends AbstractEntry.Builder<HashMap<String, V>, MapEntry<V>, MapEntry.Builder<V>> {
        public Builder() {
            super(new HashMap<>());
        }

        public Builder<V> addPair(String key, V value) {
            this.value.put(key, value);
            return this;
        }

        @Override
        protected MapEntry<V> createEntry(HashMap<String, V> defaultValue) {
            return new MapEntry<>(defaultValue);
        }
    }

    public static <V> MapEntry.Builder<V> builder() {
        return new MapEntry.Builder<>();
    }
}
