package it.hurts.shatterbyte.shatterlib.module.config.dev;

import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;

public class ExampleConfig extends ShatterConfig {
//    private SimpleEntry<RelicData> data = SimpleEntry.builder(new RelicData())
//            .build();

    @Override
    public String getPath() {
        return "example";
    }
}
