package it.hurts.shatterbyte.shatterlib.module.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.type.BooleanEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.ColorEntry;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import lombok.Getter;

@Getter
public class MyConfig extends ShatterConfig {
    private ColorEntry testColor = ColorEntry.builder(new ShatterColor(1f, 0.2f, 1f, 1f))
            .includeAlpha(true)
            .withComment("test comment!")
            .withInlineComment("yay inline comment :D")
            .build();

    private BooleanEntry testBoolean = BooleanEntry.builder(false)
            .withComment("Test boolean comment!")
            .build();

    private BooleanEntry anotherTestBoolean = BooleanEntry.builder(true)
            .withComment("Another test boolean comment!")
            .build();

    @Override
    public String getPath() {
        return ShatterLib.MODID;
    }
}