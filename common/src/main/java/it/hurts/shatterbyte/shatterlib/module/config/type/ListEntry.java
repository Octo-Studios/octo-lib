package it.hurts.shatterbyte.shatterlib.module.config.type;

import java.util.ArrayList;
import java.util.HashMap;

public class ListEntry<V> extends AbstractEntry<ArrayList<V>, ListEntry<V>> {
    private ListEntry(ArrayList<V> defaultValue) {
        super(defaultValue);
    }

    public static class Builder<V> extends AbstractEntry.Builder<ArrayList<V>, ListEntry<V>, ListEntry.Builder<V>> {
        public Builder() {
            super(new ArrayList<>());
        }

        public Builder<V> add(V value) {
            this.value.add(value);
            return this;
        }

        @Override
        protected ListEntry<V> createEntry(ArrayList<V> defaultValue) {
            return new ListEntry<>(defaultValue);
        }
    }

    public static <V> ListEntry.Builder<V> builder() {
        return new ListEntry.Builder<>();
    }
}
