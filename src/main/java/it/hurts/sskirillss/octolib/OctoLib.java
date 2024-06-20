package it.hurts.sskirillss.octolib;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.GsonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.GsonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import java.util.EnumSet;
import java.util.Set;

@Mod(OctoLib.MODID)
public class OctoLib {
    public static final String MODID = "octolib";

    public OctoLib(IEventBus bus, ModContainer container) {
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
    }
}