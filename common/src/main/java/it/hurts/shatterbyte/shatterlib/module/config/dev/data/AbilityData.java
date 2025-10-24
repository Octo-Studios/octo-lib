package it.hurts.shatterbyte.shatterlib.module.config.dev.data;

import it.hurts.shatterbyte.shatterlib.module.config.type.MapEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.SimpleEntry;

public class AbilityData {
    private MapEntry<StatData> stats = MapEntry.<StatData>builder()
            .addPair("stat1", new StatData())
            .addPair("stat2", new StatData())
            .addPair("stat3", new StatData())
            .build();
}