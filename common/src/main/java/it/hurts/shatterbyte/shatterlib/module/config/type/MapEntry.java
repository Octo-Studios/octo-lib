package it.hurts.shatterbyte.shatterlib.module.config.type;

import java.util.Map;

public class MapEntry<V> extends AbstractEntry<Map<String, V>, MapEntry<V>> {
    private MapEntry(Map<String, V> defaultValue) {
        super(defaultValue);
    }

    public static class Builder<V> extends AbstractEntry.Builder<Map<String, V>, MapEntry<V>, MapEntry.Builder<V>> {
        public Builder(Map<String, V> defaultValue) {
            super(defaultValue);
        }

        @Override
        protected MapEntry<V> createEntry(Map<String, V> defaultValue) {
            return new MapEntry<>(defaultValue);
        }
    }

    public static <V> MapEntry.Builder<V> builder(Map<String, V> defaultValue) {
        return new MapEntry.Builder<>(defaultValue);
    }
}
