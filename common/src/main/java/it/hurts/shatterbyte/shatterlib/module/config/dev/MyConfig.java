package it.hurts.shatterbyte.shatterlib.module.config.dev;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigSide;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.data.TestObject;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Name;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Range;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import lombok.Getter;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    @Name("Some random test boolean")
    private boolean testBool = false;

    @Range(min = 0, max = 10, step = 0.1d)
    private float testValue = 3.5f;

    private Rarity testRarity = Rarity.EPIC;

    private ArrayList<String> stringList = new ArrayList<>() {{
        add("abc");
        add("def");
        add("ghi");
    }};

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