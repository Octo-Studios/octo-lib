package it.hurts.shatterbyte.shatterlib.module.config;

import com.google.gson.reflect.TypeToken;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.type.ListEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.MapEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.SimpleEntry;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import lombok.Getter;

@Getter
public class MyConfig extends ShatterConfig {
    private MapEntry<ShatterColor> colorMap = MapEntry.<ShatterColor>builder()
            .put("firstColor", ShatterColor.BLUE)
            .put("secondColor", ShatterColor.RED)
            .build();

    private ListEntry<ShatterColor> colorList = ListEntry.<ShatterColor>builder()
            .add(ShatterColor.BLUE)
            .add(ShatterColor.RED)
            .build();

    private ListEntry<Boolean> booleanList = ListEntry.<Boolean>builder()
            .add(true)
            .add(false)
            .build();

    private MapEntry<ShatterColor> colorMap2 = MapEntry.<ShatterColor>builder()
            .put("firstColor", ShatterColor.BLACK)
            .put("secondColor", ShatterColor.WHITE)
            .build();

    private SimpleEntry<ShatterColor> someColor = SimpleEntry.builder(ShatterColor.GREEN)
            .withComment("Comment!!!")
            .build();

    @Override
    public String getPath() {
        return ShatterLib.MODID;
    }
}