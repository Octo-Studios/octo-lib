package it.hurts.shatterbyte.byteapi.module.config.dev;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.shatterbyte.byteapi.ByteAPI;
import it.hurts.shatterbyte.byteapi.module.config.ConfigSide;
import it.hurts.shatterbyte.byteapi.module.config.ShatterConfig;
import it.hurts.shatterbyte.byteapi.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.byteapi.util.ShatterColor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

public class MyClientConfig extends ShatterConfig {
    private String string1 = "hello";
    private float float2 = 73.5f;
    @Comment("Some color")
    private ShatterColor color = new ShatterColor(1f, 0.25f, 0.1f, 1f);
    private Identifier identifier = Identifier.fromNamespaceAndPath("byte","test");
    private Rarity rarity = Rarity.COMMON;
    private Item item = Items.DIAMOND;

    @Override
    public String getName() {
        return "byte";
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
