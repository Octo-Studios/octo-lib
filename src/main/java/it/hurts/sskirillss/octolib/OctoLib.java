package it.hurts.sskirillss.octolib;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import it.hurts.sskirillss.octolib.init.OctoParticles;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

import java.util.EnumSet;
import java.util.Set;

@Mod(OctoLib.MODID)
public class OctoLib {
    public static final String MODID = "octolib";

    public OctoLib() {
        MinecraftForge.EVENT_BUS.register(this);

        Configuration.setDefaults(new Configuration.Defaults() {
            @Override
            public JsonProvider jsonProvider() {
                return new GsonJsonProvider();
            }

            @Override
            public MappingProvider mappingProvider() {
                return new GsonMappingProvider();
            }

            @Override
            public Set<Option> options() {
                return EnumSet.of(Option.DEFAULT_PATH_LEAF_TO_NULL);
            }
        });

        OctoParticles.register();
    }
}