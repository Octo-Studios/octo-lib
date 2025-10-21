package it.hurts.shatterbyte.shatterlib.module.config.type;

import de.marhali.json5.Json5;
import de.marhali.json5.Json5Element;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;

public class ColorEntry extends AbstractEntry<ShatterColor> {
    public ColorEntry(ShatterColor defaultValue) {
        super(defaultValue);
    }

    @Override
    public void loadFromJson(Json5 json, Json5Element element) {

    }

    @Override
    public Json5Element saveToJson(Json5 json) {
        return null;
    }
}
