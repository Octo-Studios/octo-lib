package it.hurts.shatterbyte.shatterlib.neoforge;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import it.hurts.shatterbyte.shatterlib.module.command.ShatterLibClientCommand;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;

public final class ShatterLibNeoForgeClientCommands {
    private ShatterLibNeoForgeClientCommands() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("shatterlib-client")
                .then(Commands.literal("config")
                        .then(Commands.literal("reload")
                                .then(Commands.literal("all")
                                        .executes(context -> ShatterLibClientCommand.reloadAll(message -> context.getSource().sendSuccess(() -> message, false))))
                                .then(Commands.argument("path", StringArgumentType.string())
                                        .suggests((context, builder) -> SharedSuggestionProvider.suggest(ConfigManager.getClientPaths().stream().map(string -> '"' + string + '"'), builder))
                                        .executes(context -> ShatterLibClientCommand.reloadOne(
                                                context.getArgument("path", String.class),
                                                message -> context.getSource().sendSuccess(() -> message, false)
                                        ))))));
    }
}
