package it.hurts.shatterbyte.shatterlib.module.config.type.adapter;

import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Primitive;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationAdapter implements TypeAdapter<ResourceLocation> {
    @Override
    public Json5Element encode(ResourceLocation value) {
        return Json5Primitive.fromString(value.toString());
    }

    @Override
    public ResourceLocation decode(Json5Element json) {
        return ResourceLocation.parse(json.getAsString());
    }
}
