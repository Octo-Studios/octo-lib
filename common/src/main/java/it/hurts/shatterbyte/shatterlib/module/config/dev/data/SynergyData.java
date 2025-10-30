package it.hurts.shatterbyte.shatterlib.module.config.dev.data;

import it.hurts.shatterbyte.shatterlib.module.config.type.MapEntry;

public class SynergyData {
    private MapEntry<StatData> stats = MapEntry.<StatData>builder()
            .put("stat1", new StatData())
            .put("stat2", new StatData())
            .put("stat3", new StatData())
            .build();
}