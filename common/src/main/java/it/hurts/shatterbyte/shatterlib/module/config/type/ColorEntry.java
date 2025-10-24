package it.hurts.shatterbyte.shatterlib.module.config.type;

import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Primitive;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;

public class ColorEntry extends AbstractEntry<ShatterColor, ColorEntry> {
    private boolean includeAlpha;

    private ColorEntry(ShatterColor defaultValue, boolean includeAlpha) {
        super(defaultValue);
        this.includeAlpha = includeAlpha;
    }

    public static class Builder extends AbstractEntry.Builder<ShatterColor, ColorEntry, Builder> {
        private boolean includeAlpha = true;

        public Builder(ShatterColor defaultValue) {
            super(defaultValue);
        }

        public ColorEntry.Builder includeAlpha(boolean includeAlpha) {
            this.includeAlpha = includeAlpha;
            return this;
        }

        @Override
        protected ColorEntry createEntry(ShatterColor value) {
            return new ColorEntry(value, includeAlpha);
        }
    }

    public static Builder builder(ShatterColor defaultValue) {
        return new Builder(defaultValue);
    }

    @Override
    public void loadFromJson(Json5Element element) {
        String hex = element.getAsString().trim();

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        } else if (hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = hex.substring(2);
        }

        int value;
        if (includeAlpha) {
            value = (int) Long.parseLong(hex, 16);
        } else {
            int rgb = (int) Long.parseLong(hex, 16);
            value = 0xFF000000 | rgb;
        }

        this.setValue(new ShatterColor(value));
    }

    @Override
    public Json5Element saveToJson(ShatterColor value) {
        int argb = value.getARGB();
        String hex;

        if (includeAlpha) {
            hex = String.format("#%08X", argb);
        } else {
            int rgb = argb & 0xFFFFFF;
            hex = String.format("#%06X", rgb);
        }

        return Json5Primitive.fromString(hex);
    }
}
