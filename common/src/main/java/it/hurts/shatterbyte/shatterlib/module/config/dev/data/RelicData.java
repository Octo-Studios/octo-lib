package it.hurts.shatterbyte.shatterlib.module.config.dev.data;

import it.hurts.shatterbyte.shatterlib.module.config.type.SimpleEntry;

public class RelicData {
    private SimpleEntry<AbilitiesData> abilities = SimpleEntry.builder(new AbilitiesData())
            .build();
}