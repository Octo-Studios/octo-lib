package it.hurts.sskirillss.octolib;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;
import java.util.Set;

@Mod(OctoLib.MODID)
public class OctoLib {
    public static final String MODID = "octolib";

    public OctoLib() {
        Configuration.setDefaults(new Configuration.Defaults() {
            private final JsonProvider jsonProvider = new GsonJsonProvider();
            private final MappingProvider mappingProvider = new GsonMappingProvider();

            @Override
            public JsonProvider jsonProvider() {
                return jsonProvider;
            }

            @Override
            public MappingProvider mappingProvider() {
                return mappingProvider;
            }

            @Override
            public Set<Option> options() {
                return EnumSet.of(Option.DEFAULT_PATH_LEAF_TO_NULL);
            }
        });
    }
}