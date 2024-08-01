package it.hurts.sskirillss.octolib;

import java.util.HashMap;
import java.util.Map;

public class TestExtraConfig {
    public Map<Integer, String> NUMBERS = new HashMap<>();

    {
        NUMBERS.put(1, "One");
        NUMBERS.put(2, "Two");
        NUMBERS.put(3, "Three");
        NUMBERS.put(4, "Four");
        NUMBERS.put(5, "Five");
    }
}