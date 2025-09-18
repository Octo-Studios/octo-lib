package it.hurts.shatterbyte.shatterlib.neoforge;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(ShatterLib.MODID)
public final class ShatterLibNeoForge {
    public ShatterLibNeoForge(IEventBus modBus) {
        ShatterLib.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
            new ShatterLibNeoForgeClient(modBus);
    }
}
