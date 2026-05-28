package it.hurts.shatterbyte.shatterlib.module.config.dev;

import it.hurts.shatterbyte.shatterlib.module.config.ConfigSide;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.data.RelicData;

public class ExampleConfig extends ShatterConfig {
    private RelicData data = new RelicData();

    @Override
    public String getComment() {
        return """
                Example relic-like configuration file avavavavava
                Used for testing purposes.
                """;
    }

    @Override
    public String getName() {
        return "iudshuidshuidsahuidsahuidsahuidauidashuidashuida";
    }

    @Override
    public ConfigSide getSide() {
        return ConfigSide.COMMON;
    }

    @Override
    public int getSchemaVersion() {
        return 0;
    }
}
