package it.hurts.shatterbyte.byteapi.neoforge;

import it.hurts.shatterbyte.byteapi.ByteAPI;
import it.hurts.shatterbyte.byteapi.ByteAPIClient;
import it.hurts.shatterbyte.byteapi.client.config.MultipleConfigScreen;
import it.hurts.shatterbyte.byteapi.platform.ByteAPIServices;
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

@Mod(ByteAPI.MOD_ID)
public final class ByteAPINeoForge {
    public ByteAPINeoForge(IEventBus modBus, ModContainer container) {
        ByteAPIServices.initialize(new NeoForgePlatformHelper(modBus));
        ByteAPI.init();
        ByteAPI.onCommonSetup();

        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
        NeoForge.EVENT_BUS.addListener(this::onServerStarting);
        NeoForge.EVENT_BUS.addListener(this::onPlayerLoggedIn);

        if (FMLLoader.getCurrent().getDist() == Dist.CLIENT) {
            ByteAPIClient.init();
            NeoForge.EVENT_BUS.addListener(this::onRegisterClientCommands);
            NeoForge.EVENT_BUS.addListener(this::onClientTick);
            NeoForge.EVENT_BUS.addListener(this::onClientDisconnect);
            container.registerExtensionPoint(IConfigScreenFactory.class, (mod, prevScreen) -> new MultipleConfigScreen(ByteAPI.MOD_ID, prevScreen));
        }
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        ByteAPI.registerCommands(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    private void onServerStarting(ServerStartingEvent event) {
        ByteAPI.onServerBeforeStart(event.getServer());
    }

    private void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer player) {
            ByteAPI.onPlayerJoin(player);
        }
    }

    private void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        ByteAPINeoForgeClientCommands.register(event.getDispatcher());
    }

    private void onClientTick(ClientTickEvent.Pre event) {
        if (Minecraft.getInstance().level != null) {
            ByteAPIClient.onClientLevelPre(Minecraft.getInstance().level);
        }
    }

    private void onClientDisconnect(ClientPlayerNetworkEvent.LoggingOut event) {
        ByteAPIClient.onClientDisconnect();
    }
}
