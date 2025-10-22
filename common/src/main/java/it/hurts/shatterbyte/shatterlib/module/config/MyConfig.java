package it.hurts.shatterbyte.shatterlib.module.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.type.ColorEntry;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import lombok.Getter;

@Getter
public class MyConfig extends ShatterConfig {
    private ColorEntry color = ColorEntry.builder(new ShatterColor(1f, 0.2f, 1f, 1f))
            .includeAlpha(true)
            .withComment("test comment")
            .withInlineComment("yay inline comment")
            .build();

    @Override
    public String getPath() {
        return ShatterLib.MODID;
    }
}