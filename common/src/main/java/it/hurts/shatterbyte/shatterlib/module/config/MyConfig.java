package it.hurts.shatterbyte.shatterlib.module.config;

import it.hurts.shatterbyte.shatterlib.client.shake.ShakeData;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.RangeProp;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.SimpleProp;

public class MyConfig extends ShatterConfig {
    @SimpleProp(comment = "test", inlineComment = "inline test")
    public String testValue = "Default";

    @RangeProp(comment = "Test range prop", min = 0f, max = 100f)
    public float testRange = 23.5f;

    @SimpleProp(comment = "Test object", inlineComment = "bruh")
    public ShakeData object = new ShakeData(1f, 2f, 10);
}