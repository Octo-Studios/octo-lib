package it.hurts.shatterbyte.byteapi.module.config.dev.data;

import java.util.LinkedHashMap;
import java.util.Map;

public class AbilityData {
    private Map<String, StatData> stats = new LinkedHashMap<>() {{
        put("stat1", new StatData());
        put("stat2", new StatData());
        put("stat3", new StatData());
    }};
}