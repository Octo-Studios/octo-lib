package it.hurts.shatterbyte.shatterlib.module.config.type;

public class SimpleEntry<T> extends AbstractEntry<T, SimpleEntry<T>> {
    private SimpleEntry(T defaultValue) {
        super(defaultValue);
    }

    public static class Builder<T> extends AbstractEntry.Builder<T, SimpleEntry<T>, SimpleEntry.Builder<T>> {
        public Builder(T defaultValue) {
            super(defaultValue);
        }

        @Override
        protected SimpleEntry<T> createEntry(T defaultValue) {
            return new SimpleEntry<>(defaultValue);
        }
    }

    public static <T> SimpleEntry.Builder<T> builder(T defaultValue) {
        return new SimpleEntry.Builder<>(defaultValue);
    }
}
