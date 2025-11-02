package it.hurts.shatterbyte.shatterlib.module.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.dev.data.Inherite;
import it.hurts.shatterbyte.shatterlib.module.config.dev.data.TestObject;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Range;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MyConfig extends ShatterConfig {
    @Override
    public String getComment() {
        return """
                This is an example config,
                Feel free to familiarize yourself with the features of ShatterLib's configuration system!
                
                :P
                - catboybinary
                """;
    }

    @Comment("Test comment!")
    private Map<String, ShatterColor> colorMap = new LinkedHashMap<>() {{
        put("test1", ShatterColor.BLACK);
        put("test2", ShatterColor.RED);
        put("test3", ShatterColor.BLUE);
    }};

    private List<ShatterColor> colorList = new ArrayList<>() {{
        add(ShatterColor.WHITE);
        add(ShatterColor.GREEN);
    }};

    private List<Boolean> booleanList = new ArrayList<>() {{
        add(false);
        add(true);
    }};

    private Map<String, ShatterColor> colorMap2 = new LinkedHashMap<>() {{
        put("test14", ShatterColor.BLACK);
        put("test5", ShatterColor.RED);
        put("test6", ShatterColor.BLUE);
    }};

    @Range(max = 0)
    private double test = -0.1;

    @Comment("test comment!")
    private ShatterColor someColor = ShatterColor.GREEN;

    @Comment("Test enum!")
    private Rarity testRarity = Rarity.EPIC;

    @Exclude
    private String superPrivateString = "pls don't";

    private ResourceLocation someResourceLocation = ResourceLocation.fromNamespaceAndPath(ShatterLib.MODID, "test_location");

    private TestObject someObject = new TestObject();
    private Inherite inheritanceTest = new Inherite();

    @Override
    public String getPath() {
        return ShatterLib.MODID;
    }
}