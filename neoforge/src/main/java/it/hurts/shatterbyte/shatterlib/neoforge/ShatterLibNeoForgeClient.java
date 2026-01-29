package it.hurts.shatterbyte.shatterlib.neoforge;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.ShatterLibClient;
import it.hurts.shatterbyte.shatterlib.client.config.MultipleConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = ShatterLib.MOD_ID, dist = Dist.CLIENT)
public final class ShatterLibNeoForgeClient {
    public ShatterLibNeoForgeClient(IEventBus modBus, ModContainer container) {
        ShatterLibClient.init();
        container.registerExtensionPoint(IConfigScreenFactory.class, (mod, prevScreen) -> new MultipleConfigScreen(ShatterLib.MOD_ID, prevScreen));
    }
}
