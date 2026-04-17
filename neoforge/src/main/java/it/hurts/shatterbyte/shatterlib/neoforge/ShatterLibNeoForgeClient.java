package it.hurts.shatterbyte.shatterlib.neoforge;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.ShatterLibClient;
import it.hurts.shatterbyte.shatterlib.client.config.MultipleConfigScreen;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ShatterLib.MOD_ID, dist = Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public final class ShatterLibNeoForgeClient {
    public ShatterLibNeoForgeClient(IEventBus modBus, ModContainer container) {
        ShatterLibClient.init();
        //container.registerExtensionPoint(IConfigScreenFactory.class, (mod, prevScreen) -> new MultipleConfigScreen(ShatterLib.MOD_ID, prevScreen));
    }

    @SubscribeEvent
    public static void onClientSetup(FMLConstructModEvent e) {
        String modId = e.getContainer().getModId();
        if (!ConfigManager.CONFIGS_BY_MODID.containsKey(modId)) {
            return;
        }

        e.getContainer().registerExtensionPoint(IConfigScreenFactory.class, (mod, prevScreen) -> new MultipleConfigScreen(modId, prevScreen));
    }
}
