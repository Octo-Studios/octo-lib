package it.hurts.shatterbyte.shatterlib.module.config.type.adapter;

import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Primitive;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class ItemAdapter implements TypeAdapter<Item> {
    @Override
    public Json5Element encode(Item value) {
        return Json5Primitive.fromString(value.arch$registryName().toString());
    }

    @Override
    public Item decode(Json5Element json) {
        return BuiltInRegistries.ITEM.getValue(Identifier.parse(json.getAsString()));
    }
}
