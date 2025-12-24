package it.hurts.shatterbyte.shatterlib.module.config.dev;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigSide;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.data.TestObject;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Range;
import lombok.Getter;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;

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

//    @Comment("Test comment!")
//    private Map<String, ShatterColor> colorMap = new LinkedHashMap<>() {{
//        put("test1", ShatterColor.BLACK);
//        put("test2", ShatterColor.RED);
//        put("test3", ShatterColor.BLUE);
//        put("addedByFixer", ShatterColor.GREEN);
//    }};
//
//    private List<ShatterColor> colorList = new ArrayList<>() {{
//        add(ShatterColor.WHITE);
//        add(ShatterColor.GREEN);
//    }};
//
//    private List<Boolean> booleanList = new ArrayList<>() {{
//        add(false);
//        add(true);
//    }};
//
//    private Map<String, ShatterColor> colorMap2 = new LinkedHashMap<>() {{
//        put("test14", ShatterColor.BLACK);
//        put("test5", ShatterColor.RED);
//        put("test6", ShatterColor.BLUE);
//    }};

//    @Range(min = 0, max = 1)
//    private double test = -0.1;

//    @Comment("test comment!")
//    private ShatterColor someColor = ShatterColor.GREEN;

//    @Comment("Test enum!")
//    private Rarity testRarity = Rarity.EPIC;
//
//    @Exclude
//    private String superPrivateString = "pls don't";
    private String someString = "Test String!!!";

    private TestObject someObject = new TestObject();

    @Range(min = 0, max = 10, step = 0.1d)
    private float testValue = 3.5f;

    private ArrayList<Boolean> list = new ArrayList<>() {{
        add(false);
        add(true);
        add(true);
    }};

    private Rarity testRarity = Rarity.EPIC;
//    private ResourceLocation someResourceLocation = ResourceLocation.fromNamespaceAndPath(ShatterLib.MODID, "test_location");
//
//    private Inherite inheritanceTest = new Inherite();


    @Override
    public String getName() {
        return ShatterLib.MOD_ID;
    }

    @Override
    public ConfigSide getSide() {
        return ConfigSide.COMMON;
    }

    @Override
    public int getSchemaVersion() {
        return 2;
    }
}