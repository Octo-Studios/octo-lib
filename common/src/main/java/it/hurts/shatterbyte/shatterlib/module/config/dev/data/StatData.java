package it.hurts.shatterbyte.shatterlib.module.config.dev.data;

import it.hurts.shatterbyte.shatterlib.module.config.type.SimpleEntry;

public class StatData {
    private SimpleEntry<Double> min = SimpleEntry.builder(0D)
            .build();

    private SimpleEntry<Double> max = SimpleEntry.builder(1D)
            .build();
}