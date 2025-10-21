package it.hurts.shatterbyte.shatterlib.module.config;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.client.shake.ShakeData;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.RangeProp;
import it.hurts.shatterbyte.shatterlib.module.config.annotation.SimpleProp;
import lombok.Getter;

@Getter
public class MyConfig extends ShatterConfig {
    @SimpleProp(comment = "test", inlineComment = "inline test")
    private String testValue = "Default";

    @RangeProp(min = 0f, stringFormat = "%.1f", clamp = true)
    private double testRange = 23.5d;

    @SimpleProp(comment = "Test object")
    private ShakeData shakeData = new ShakeData(1f, 2f, 10);

    @Override
    public String getPath() {
        return ShatterLib.MODID;
    }
}