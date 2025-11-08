package it.hurts.shatterbyte.shatterlib.module.config.dev;

import com.mojang.blaze3d.vertex.PoseStack;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigSide;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;

public class MyClientConfig extends ShatterConfig {
    @Comment("bleh")
    private PoseStack superClientThingy = new PoseStack();

    @Override
    public String getPath() {
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
