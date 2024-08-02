package it.hurts.sskirillss.octolib;

import it.hurts.sskirillss.octolib.config.annotations.Prop;
import it.hurts.sskirillss.octolib.config.impl.OctoConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class TestConfig implements OctoConfig {
    @Prop(comment = "lolololololo")
    public int meow = 5;

    public String honk = "HONK";

    public double oink = 4.4D;

    public Pair<String, String> woof = Pair.of("WOOF_1", "WOOF_2");
    
    public TestExtraConfig config = new TestExtraConfig();

    @Override
    public Object prepareData() {
        return this;
    }

    @Override
    public void onLoadObject(Object object) {

    }
}