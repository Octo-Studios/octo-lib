package it.hurts.shatterbyte.shatterlib.module.config.type.adapter;

import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Primitive;
import net.minecraft.resources.Identifier;

public class IdentifierAdapter implements TypeAdapter<Identifier> {
    @Override
    public Json5Element encode(Identifier value) {
        return Json5Primitive.fromString(value.toString());
    }

    @Override
    public Identifier decode(Json5Element json) {
        return Identifier.parse(json.getAsString());
    }
}
