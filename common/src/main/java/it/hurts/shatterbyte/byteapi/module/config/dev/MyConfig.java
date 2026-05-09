package it.hurts.shatterbyte.byteapi.module.config.dev;

import io.netty.channel.ChannelHandler;
import it.hurts.shatterbyte.byteapi.ByteAPI;
import it.hurts.shatterbyte.byteapi.module.config.ConfigSide;
import it.hurts.shatterbyte.byteapi.module.config.ShatterConfig;
import it.hurts.shatterbyte.byteapi.module.config.dev.data.TestObject;
import it.hurts.shatterbyte.byteapi.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.byteapi.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.byteapi.module.config.type.annotation.Range;
import lombok.Getter;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
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
        return ByteAPI.MOD_ID;
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


