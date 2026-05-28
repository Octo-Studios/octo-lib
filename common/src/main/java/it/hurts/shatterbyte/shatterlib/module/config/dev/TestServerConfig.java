package it.hurts.shatterbyte.shatterlib.module.config.dev;

import it.hurts.shatterbyte.shatterlib.module.config.ConfigSide;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;

public class TestServerConfig extends ShatterConfig {
    @Comment("Some value!")
    private int someValue = 123;
    @Comment("Some other value!")
    private String someOtherValue = "im a char array in disguise";


    @Override
    public String getName() {
        return "byte";
    }

    @Override
    public int getSchemaVersion() {
        return 0;
    }

    @Override
    public ConfigSide getSide() {
        return ConfigSide.SERVER;
    }
}
