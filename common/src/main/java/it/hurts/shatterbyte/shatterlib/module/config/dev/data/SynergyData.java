package it.hurts.shatterbyte.shatterlib.module.config.dev.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class SynergyData {
    private Map<String, StatData> stats = new LinkedHashMap<>() {{
        put("stat1", new StatData());
        put("stat2", new StatData());
        put("stat3", new StatData());
    }};
}