package it.hurts.shatterbyte.shatterlib.neoforge;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.ShatterLibClient;
import it.hurts.shatterbyte.shatterlib.client.config.MultipleConfigScreen;
import it.hurts.shatterbyte.shatterlib.platform.ShatterLibServices;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(ShatterLib.MOD_ID)
public final class ShatterLibNeoForge {
    public ShatterLibNeoForge(IEventBus modBus, ModContainer container) {
        ShatterLibServices.initialize(new NeoForgePlatformHelper(modBus));
        ShatterLib.init();
        ShatterLib.onCommonSetup();

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);

        if (FMLLoader.getCurrent().getDist() == Dist.CLIENT) {
            ShatterLibClient.init();
            NeoForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
            NeoForge.EVENT_BUS.addListener(this::onClientTick);
            NeoForge.EVENT_BUS.addListener(this::onClientDisconnect);
            //container.registerExtensionPoint(IConfigScreenFactory.class, (mod, prevScreen) -> new MultipleConfigScreen(ShatterLib.MOD_ID, prevScreen));
        }
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ShatterLib.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    private void onServerStarting(ServerStartingEvent event) {
        ShatterLib.onServerBeforeStart(event.getServer());
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            ShatterLib.onPlayerJoin(player);
        }
    }

    private void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        ShatterLibNeoForgeClientCommands.register(event.getDispatcher());
    }

    private void onClientTick(ClientTickEvent.Pre event) {
        if (Minecraft.getInstance().level != null) {
            ShatterLibClient.onClientLevelPre(Minecraft.getInstance().level);
        }
    }

    private void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ShatterLibClient.onClientDisconnect();
    }
}
