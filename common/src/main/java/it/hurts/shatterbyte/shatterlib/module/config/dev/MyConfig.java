package it.hurts.shatterbyte.shatterlib.module.config.dev;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigSide;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.dev.data.TestObject;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Range;
import lombok.Getter;

import java.util.LinkedHashMap;

@Getter
public class MyConfig extends ShatterConfig {
    @Override
    public String getComment() {
        return """
                This is an example config
                Feel free to familiarize yourself with the features of ByteAPI's configuration system!
                
                :P
                - catboybinary
                """;
    }

    private LinkedHashMap<String, TestObject> someMap = new LinkedHashMap<>() {{
        put("a", new TestObject());
    }};

    @Range(min = 0, max = 5, step = 0.25f)
    private LinkedHashMap<String, Float> map = new LinkedHashMap<>() {{
        put("abc", 1f);
        put("def", 2f);
        put("ghi", 3.5f);
    }};


    @Override
    public String getName() {
        return ShatterLib.MOD_ID;
    }

    @Override
    public ConfigSide getSide() {
        return ConfigSide.COMMON;
    }

    @Override
    public int getSchemaVersion() {
        return 2;
    }
}


