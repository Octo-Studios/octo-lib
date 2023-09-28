package it.hurts.sskirillss.octolib.config.api;

import com.jayway.jsonpath.JsonPath;
import it.hurts.sskirillss.octolib.config.api.events.ConfigConstructEvent;
import it.hurts.sskirillss.octolib.config.data.ConfigContext;
import it.hurts.sskirillss.octolib.config.data.OctoConfig;
import it.hurts.sskirillss.octolib.config.storage.ConfigStorage;
import it.hurts.sskirillss.octolib.config.utils.ConfigUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.registries.RegisterEvent;

import javax.annotation.Nullable;
import java.nio.file.Files;
import java.nio.file.Path;

public interface IOctoConfig {
    Path getPath();

    @Nullable
    default <T extends IOctoConfig> T setup() {
        OctoConfig config = new OctoConfig(this);

        ConfigConstructEvent event = new ConfigConstructEvent(this, new ConfigContext(JsonPath.parse(ConfigUtils.SERIALIZER.toJson(this))));

        ModLoader.get().postEventWrapContainerInModOrder(event);

        if (!event.isCanceled()) {
            ConfigContext context = event.getContext();

            config.setSchema(context);

            if (Files.exists(getPath()))
                config.loadFromFile();
            else {
                config.setData(context);

                config.saveToFile();
            }

            ConfigStorage.set(config);
        }

        return (T) event.getConstructor();
    }
}