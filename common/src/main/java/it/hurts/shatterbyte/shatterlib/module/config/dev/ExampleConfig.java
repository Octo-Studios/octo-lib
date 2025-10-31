package it.hurts.shatterbyte.shatterlib.module.config.dev;

import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.data.RelicData;

public class ExampleConfig extends ShatterConfig {
    private RelicData data = new RelicData();

    @Override
    public String getPath() {
        return "example";
    }
}
