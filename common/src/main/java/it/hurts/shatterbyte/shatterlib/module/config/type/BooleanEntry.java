package it.hurts.shatterbyte.shatterlib.module.config.type;

import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Primitive;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;

public class BooleanEntry extends AbstractEntry<Boolean, BooleanEntry> {
    private BooleanEntry(boolean defaultValue) {
        super(defaultValue);
    }

    public static class Builder extends AbstractEntry.Builder<Boolean, BooleanEntry, Builder> {
        public Builder(boolean value) {
            super(value);
        }

        @Override
        protected BooleanEntry createEntry(Boolean value) {
            return new BooleanEntry(value);
        }
    }

    public static Builder builder(boolean value) {
        return new Builder(value);
    }

    @Override
    public void loadFromJson(Json5Element element) {
        this.setValue(element.getAsBoolean());
    }

    @Override
    public Json5Element saveToJson() {
        return Json5Primitive.fromBoolean(this.getValue());
    }
}
