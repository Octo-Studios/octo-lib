package it.hurts.shatterbyte.byteapi.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import it.hurts.shatterbyte.byteapi.module.command.ByteAPIClientCommand;
import it.hurts.shatterbyte.byteapi.module.config.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;

public final class ByteAPIFabricClientCommands {
    private ByteAPIFabricClientCommands() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("byte-client")
                .then(ClientCommands.literal("config")
                        .then(ClientCommands.literal("reload")
                                .then(ClientCommands.literal("all")
                                        .executes(context -> ByteAPIClientCommand.reloadAll(message -> context.getSource().getPlayer().sendOverlayMessage(message))))
                                .then(ClientCommands.argument("path", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(ConfigManager.getClientPaths().stream().map(string -> '"' + string + '"'), builder))
                                        .executes(context -> ByteAPIClientCommand.reloadOne(
                                                context.getArgument("path", String.class),
                                                message -> context.getSource().getPlayer().sendOverlayMessage(message)
                                        ))))));
    }
}
