package it.hurts.shatterbyte.shatterlib.module.config.dev;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigSide;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;

public class MyClientConfig extends ShatterConfig {
    private String string1 = "hello";
    private float float2 = 73.5f;
    @Comment("Some color")
    private ShatterColor color = new ShatterColor(1f, 0.25f, 0.1f, 1f);
    private Identifier identifier = Identifier.fromNamespaceAndPath(ShatterLib.MOD_ID,"test");
    private Rarity rarity = Rarity.COMMON;
    private Item item = Items.DIAMOND;
    private ArrayList<Item> itemList = new ArrayList<>();

    @Override
    public String getName() {
        return ShatterLib.MOD_ID;
    }

    @Override
    public ConfigSide getSide() {
        return ConfigSide.CLIENT;
    }


    @Override
    public int getSchemaVersion() {
        return 0;
    }
}
