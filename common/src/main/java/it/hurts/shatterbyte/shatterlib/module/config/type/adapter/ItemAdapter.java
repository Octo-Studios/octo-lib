package it.hurts.shatterbyte.shatterlib.module.config.type.adapter;

import de.marhali.json5.Json5Element;
import de.marhali.json5.Json5Primitive;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ItemAdapter implements TypeAdapter<Item> {
    @Override
    public Json5Element encode(Item value) {
        return Json5Primitive.fromString(BuiltInRegistries.ITEM.getKey(value).toString());
    }

    @Override
    public Item decode(Json5Element json) {
        Identifier id = Identifier.parse(json.getAsString());
        if (!BuiltInRegistries.ITEM.containsKey(id)) {
            return Items.AIR;
        }

        return BuiltInRegistries.ITEM.getValue(id);
    }
}
