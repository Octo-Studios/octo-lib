package it.hurts.shatterbyte.byteapi.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import it.hurts.shatterbyte.byteapi.module.command.ByteAPIClientCommand;
import it.hurts.shatterbyte.byteapi.module.config.ConfigManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.neoforged.neoforge.client.ClientCommandSourceStack;

public final class ByteAPINeoForgeClientCommands {
    private ByteAPINeoForgeClientCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("byte-client")
                .then(Commands.literal("config")
                        .then(Commands.literal("reload")
                                .then(Commands.literal("all")
                                        .executes(context -> ByteAPIClientCommand.reloadAll(message -> ((ClientCommandSourceStack) context.getSource()).getPlayer().sendSystemMessage(message))))
                                .then(Commands.argument("path", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(ConfigManager.getClientPaths().stream().map(string -> '"' + string + '"'), builder))
                                        .executes(context -> ByteAPIClientCommand.reloadOne(
                                                context.getArgument("path", String.class),
                                                message -> ((ClientCommandSourceStack) context.getSource()).getPlayer().sendSystemMessage(message)
                                        ))))));
    }
}
