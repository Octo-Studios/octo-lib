package it.hurts.shatterbyte.byteapi;

import it.hurts.shatterbyte.byteapi.module.command.ByteAPICommand;
import it.hurts.shatterbyte.byteapi.module.config.ConfigManager;
import it.hurts.shatterbyte.byteapi.module.config.dev.ExampleConfig;
import it.hurts.shatterbyte.byteapi.module.config.dev.MyConfig;
import it.hurts.shatterbyte.byteapi.module.config.dev.TestServerConfig;
import it.hurts.shatterbyte.byteapi.module.config.dev.config.ShakeConfig;
import it.hurts.shatterbyte.byteapi.module.config.network.SyncServerConfigPacket;
import it.hurts.shatterbyte.byteapi.module.config.network.TestScreenPacket;
import it.hurts.shatterbyte.byteapi.module.config.util.Json5Utils;
import it.hurts.shatterbyte.byteapi.module.network.ByteAPINetwork;
import it.hurts.shatterbyte.byteapi.platform.ByteAPIServices;
import it.hurts.shatterbyte.byteapi.util.ShatterColor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.CommandDispatcher;

public final class ByteAPI {
    public static final String MOD_ID = "byte";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    public static MyConfig CONFIG = new MyConfig();
    public static TestServerConfig SERVER_CONFIG = new TestServerConfig();
    public static ExampleConfig EXAMPLE = new ExampleConfig();
    public static ShakeConfig EXAMPLE_SHAKE_CONFIG = new ShakeConfig();

    public static void init() {
        ByteAPINetwork.registerS2CPayloadType(TestScreenPacket.TYPE, TestScreenPacket.STREAM_CODEC);
        ByteAPINetwork.registerS2CPayloadType(SyncServerConfigPacket.TYPE, SyncServerConfigPacket.STREAM_CODEC);

        if (ByteAPIServices.platform().isDevelopmentEnvironment()) {
            ConfigManager.register(MOD_ID, CONFIG);
            ConfigManager.register(MOD_ID, SERVER_CONFIG);
            ConfigManager.register(MOD_ID, EXAMPLE);
            ConfigManager.register(MOD_ID, EXAMPLE_SHAKE_CONFIG);
        }
    }

    public static void onCommonSetup() {
        if (ByteAPIServices.platform().isClientEnvironment()) {
            LOGGER.info("Loading client configs");
            ConfigManager.loadAllClientConfigs();
        }

        LOGGER.info("Loading common configs");
        ConfigManager.loadAllCommonConfigs();

        LOGGER.info("Loading server configs");
        ConfigManager.loadAllServerConfigs();
    }

    public static void onServerBeforeStart(MinecraftServer server) {
        Path serverConfigFolder = ConfigManager.getServerConfigFolder(server);

        LOGGER.info("Loading server config overrides");
        ConfigManager.loadServerConfigOverrides(serverConfigFolder);
    }

    public static void onPlayerJoin(ServerPlayer player) {
        ConfigManager.syncServerConfigs(player);
    }

    public static void registerCommands(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext buildContext,
            Commands.CommandSelection commandSelection
    ) {
        ByteAPICommand.register(dispatcher, buildContext, commandSelection);
    }
}
