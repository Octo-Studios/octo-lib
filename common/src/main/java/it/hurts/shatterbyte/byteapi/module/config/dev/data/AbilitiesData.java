package it.hurts.shatterbyte.byteapi.module.config.dev.data;

import it.hurts.shatterbyte.byteapi.module.config.type.annotation.Comment;

import java.util.LinkedHashMap;
import java.util.Map;

public class AbilitiesData {
    @Comment("Abilities of this relic")
    private Map<String, AbilityData> abilities = new LinkedHashMap<>() {{
        put("ability1", new AbilityData());
        put("ability2", new AbilityData());
        put("ability3", new AbilityData());
    }};
    @Comment("Synergies of this relic")
    private Map<String, SynergyData> synergies = new LinkedHashMap<>() {{
        put("synergy1", new SynergyData());
        put("synergy2", new SynergyData());
        put("synergy3", new SynergyData());
    }};
}