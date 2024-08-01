package it.hurts.sskirillss.octolib.config.event;

import it.hurts.sskirillss.octolib.config.ConfigManager;
import it.hurts.sskirillss.octolib.config.annotations.registration.ConfigRegistration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.apache.logging.log4j.util.Cast;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ConfigEvent {
    
    private static final Type CONFIG_REGISTRATION = Type.getType(ConfigRegistration.class);
    
    @SubscribeEvent
    public static void onCommandLoad(FMLCommonSetupEvent event) {
        
        ModList.get().getAllScanData().stream()
                .map(ModFileScanData::getAnnotations)
                .flatMap(Collection::stream)
                .filter(a -> CONFIG_REGISTRATION.equals(a.annotationType()))
                .forEach(a -> {
                    try {
                        Class<?> clazz = Class.forName(a.clazz().getClassName(), true, ConfigEvent.class.getClassLoader());
                        var modId = clazz.getAnnotation(ConfigRegistration.class).modId();
                        Arrays.stream(clazz.getFields()).filter(f -> Modifier.isStatic(f.getModifiers()))
                                .forEach(f -> registerFieldConfig(f, modId));
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
        
        ConfigManager.reloadAll();
    }
    
    public static void registerFieldConfig(Field field, String modId) {
        field.setAccessible(true);
        try {
            var value = field.get(null);
            
            for (var a : field.getAnnotations()) {
                var fabric = ConfigManager.getConfigFabric(a.getClass());
                
                var octoConfig = fabric.getFirst().create(Cast.cast(a), value);
                var name = fabric.getSecond().getName(Cast.cast(a), value);
                
                var location = ResourceLocation.fromNamespaceAndPath(modId, name);
                ConfigManager.registerConfig(location, octoConfig);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
    
}
