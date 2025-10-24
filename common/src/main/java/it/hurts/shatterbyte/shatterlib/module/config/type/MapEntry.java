package it.hurts.shatterbyte.shatterlib.module.config.type;

import java.util.LinkedHashMap;

public class MapEntry<V> extends AbstractEntry<LinkedHashMap<String, V>, MapEntry<V>> {
    private MapEntry(LinkedHashMap<String, V> defaultValue) {
        super(defaultValue);
    }

    public static class Builder<V> extends AbstractEntry.Builder<LinkedHashMap<String, V>, MapEntry<V>, MapEntry.Builder<V>> {
        public Builder() {
            super(new LinkedHashMap<>());
        }

        public Builder<V> addPair(String key, V value) {
            this.value.put(key, value);
            return this;
        }

        @Override
        protected MapEntry<V> createEntry(LinkedHashMap<String, V> defaultValue) {
            return new MapEntry<>(defaultValue);
        }
    }

    public static <V> MapEntry.Builder<V> builder() {
        return new MapEntry.Builder<>();
    }
}
