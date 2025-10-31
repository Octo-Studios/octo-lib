package it.hurts.shatterbyte.shatterlib.module.config.dev.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class AbilitiesData {
    private Map<String, AbilityData> abilities = new LinkedHashMap<>() {{
        put("ability1", new AbilityData());
        put("ability2", new AbilityData());
        put("ability3", new AbilityData());
    }};
    private Map<String, SynergyData> synergies = new LinkedHashMap<>() {{
        put("synergy1", new SynergyData());
        put("synergy2", new SynergyData());
        put("synergy3", new SynergyData());
    }};
}