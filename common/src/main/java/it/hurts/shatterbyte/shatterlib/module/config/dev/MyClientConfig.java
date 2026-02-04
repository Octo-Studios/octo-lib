package it.hurts.shatterbyte.shatterlib.module.config.dev;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigSide;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;

public class MyClientConfig extends ShatterConfig {
    @Comment("Some color")
    private ShatterColor color = new ShatterColor(1f, 0.25f, 0.1f, 1f);

    @Override
    public String getName() {
        return "shatterlib";
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
