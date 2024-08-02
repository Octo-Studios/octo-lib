//package it.hurts.octostudios.octolib.modules.config.event;
//
//import it.hurts.octostudios.octolib.OctoLib;
//import it.hurts.octostudios.octolib.modules.config.ConfigManager;
//import it.hurts.octostudios.octolib.modules.config.annotations.registration.ConfigRegistration;
//import net.neoforged.bus.api.SubscribeEvent;
//import net.neoforged.fml.ModList;
//import net.neoforged.fml.common.EventBusSubscriber;
//import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
//import net.neoforged.neoforgespi.language.ModFileScanData;
//import org.apache.logging.log4j.util.Cast;
//import org.objectweb.asm.Type;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.util.Arrays;
//import java.util.Collection;
//
//@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
//public class ConfigEvent {
//
//    private static final Type CONFIG_REGISTRATION = Type.getType(ConfigRegistration.class);
//
//    @SubscribeEvent
//    public static void onCommandLoad(FMLCommonSetupEvent event) {
//
//        ModList.get().getAllScanData().stream()
//                .map(ModFileScanData::getAnnotations)
//                .flatMap(Collection::stream)
//                .filter(a -> CONFIG_REGISTRATION.equals(a.annotationType()))
//                .forEach(a -> {
//                    try {
//                        Class<?> clazz = Class.forName(a.clazz().getClassName(), true, ConfigEvent.class.getClassLoader());
//                        var dir = clazz.getAnnotation(ConfigRegistration.class).dir();
//                        Arrays.stream(clazz.getFields()).filter(f -> Modifier.isStatic(f.getModifiers()))
//                                .forEach(f -> registerFieldConfig(f, dir));
//                    } catch (ClassNotFoundException e) {
//                        throw new RuntimeException(e);
//                    }
//                });
//
//        ConfigManager.reloadAll();
//    }
//
//    public static void registerFieldConfig(Field field, String modId) {
//        field.setAccessible(true);
//        try {
//            var value = field.get(null);
//
//            for (var a : field.getAnnotations()) {
//                var fabric = ConfigManager.getConfigFabric(a.annotationType());
//
//                if (fabric == null) {
//                    OctoLib.LOGGER.warn("Unsupported annotation {} for config initialization.", a);
//                    continue;
//                }
//
//                var octoConfig = fabric.getFirst().create(Cast.cast(a), value);
//                var name = fabric.getSecond().getName(Cast.cast(a), value);
//
//                String location = modId + "/" + name;
//                ConfigManager.registerConfig(location, octoConfig);
//            }
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//}
