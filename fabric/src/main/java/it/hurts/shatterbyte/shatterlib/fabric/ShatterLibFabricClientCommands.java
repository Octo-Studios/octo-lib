package it.hurts.shatterbyte.shatterlib.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibClientCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.SharedSuggestionProvider;

public final class ShatterLibFabricClientCommands {
    private ShatterLibFabricClientCommands() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("shatterlib-client")
                .then(ClientCommands.literal("config")
                        .then(ClientCommands.literal("reload")
                                .then(ClientCommands.literal("all")
                                        .executes(context -> ShatterLibClientCommand.reloadAll(message -> context.getSource().getPlayer().sendOverlayMessage(message))))
                                .then(ClientCommands.argument("path", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(ConfigManager.getClientPaths().stream().map(string -> '"' + string + '"'), builder))
                                        .executes(context -> ShatterLibClientCommand.reloadOne(
                                                context.getArgument("path", String.class),
                                                message -> context.getSource().getPlayer().sendOverlayMessage(message)
                                        ))))));
    }
}
