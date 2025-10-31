package it.hurts.shatterbyte.shatterlib.module.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Range;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
public class MyConfig extends ShatterConfig {
    @Comment("colors")
    private Map<String, ShatterColor> colorMap = new LinkedHashMap<>();

    private List<ShatterColor> colorList = new ArrayList<>();
    private List<Boolean> booleanList = new ArrayList<>();
    private Map<String, ShatterColor> colorMap2 = new LinkedHashMap<>();

    @Range(max = 0)
    private double test = -0.1;

    @Comment("test comment!")
    private ShatterColor someColor = ShatterColor.GREEN;

    @Exclude
    private String superPrivateString = "pls don't";

    public MyConfig() {
        colorMap.put("test1", ShatterColor.BLACK);
        colorMap.put("test2", ShatterColor.RED);
        colorMap.put("test3", ShatterColor.BLUE);

        colorList.add(ShatterColor.WHITE);
        colorList.add(ShatterColor.GREEN);

        booleanList.add(false);
        booleanList.add(true);

        colorMap2.put("test14", ShatterColor.BLACK);
        colorMap2.put("test5", ShatterColor.RED);
        colorMap2.put("test6", ShatterColor.BLUE);
    }

    @Override
    public String getPath() {
        return ShatterLib.MODID;
    }
}