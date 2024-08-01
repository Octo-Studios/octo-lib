package it.hurts.sskirillss.octolib;

import it.hurts.sskirillss.octolib.config.impl.OctoConfig;
import org.apache.commons.lang3.tuple.Pair;

public class TestConfig implements OctoConfig {
    public int meow = 5;

    public String honk = "HONK";

    public double oink = 4.4D;

    public Pair<String, String> woof = Pair.of("WOOF_1", "WOOF_2");

    @Override
    public Object prepareData() {
        return this;
    }

    @Override
    public void onLoadObject(Object object) {

    }
}