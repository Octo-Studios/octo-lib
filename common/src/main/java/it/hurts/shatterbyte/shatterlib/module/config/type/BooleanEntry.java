package it.hurts.shatterbyte.shatterlib.module.config.type;

import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Primitive;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;

public class BooleanEntry extends AbstractEntry<Boolean, BooleanEntry> {
    private BooleanEntry(boolean defaultValue) {
        super(defaultValue);
    }

    public static class Builder extends AbstractEntry.Builder<Boolean, BooleanEntry, Builder> {
        public Builder(boolean defaultValue) {
            super(defaultValue);
        }

        @Override
        protected BooleanEntry createEntry(Boolean defaultValue) {
            return new BooleanEntry(defaultValue);
        }
    }

    public static Builder builder(boolean defaultValue) {
        return new Builder(defaultValue);
    }
}
