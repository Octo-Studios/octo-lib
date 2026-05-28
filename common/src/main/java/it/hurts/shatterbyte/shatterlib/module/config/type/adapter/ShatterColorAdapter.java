package it.hurts.shatterbyte.shatterlib.module.config.type.adapter;

import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Primitive;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;

public class ShatterColorAdapter implements TypeAdapter<ShatterColor> {
    @Override
    public Json5Element encode(ShatterColor value) {
        int argb = value.getARGB();
        String hex;

        if (value.a() == 1f) {
            int rgb = argb & 0xFFFFFF;
            hex = String.format("#%06X", rgb);
        } else {
            hex = String.format("#%08X", argb);
        }

        return Json5Primitive.fromString(hex);
    }

    @Override
    public ShatterColor decode(Json5Element json) {
        String hex = json.getAsString().trim();

        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        } else if (hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = hex.substring(2);
        }

        int value;
        if (hex.length() == 8) {
            value = (int) Long.parseLong(hex, 16);
        } else if (hex.length() == 6) {
            int rgb = (int) Long.parseLong(hex, 16);
            value = 0xFF000000 | rgb;
        } else {
            ShatterLib.LOGGER.warn("Invalid hex string: {}", hex);
            value = 0xFF000000;
        }

        return new ShatterColor(value);
    }
}
