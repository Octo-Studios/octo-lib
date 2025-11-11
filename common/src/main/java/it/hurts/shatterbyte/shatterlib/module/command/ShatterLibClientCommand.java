package it.hurts.shatterbyte.shatterlib.module.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.event.events.client.ClientCommandRegistrationEvent;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ShatterLibClientCommand {
    public static void register(CommandDispatcher<ClientCommandRegistrationEvent.ClientCommandSourceStack> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(ClientCommandRegistrationEvent.literal("shatterlib-client").then(ClientCommandRegistrationEvent.literal("config").then(ClientCommandRegistrationEvent.literal("reload").then(ClientCommandRegistrationEvent.literal("all").executes(context -> {
            int counter = 0;

            for (String path : ConfigManager.getClientPaths()) {
                if (!ConfigManager.reload(path, null)) {
                    context.getSource().arch$getPlayer().displayClientMessage(Component.literal("Failed to reload: ").withStyle(ChatFormatting.RED).append(Component.literal("[" + path + "]")), true);
                    continue;
                }

                counter++;
            }

            context.getSource().arch$getPlayer().displayClientMessage(Component.literal(counter + " configs reloaded successfully!"), true);
            return Command.SINGLE_SUCCESS;
        })).then(ClientCommandRegistrationEvent.argument("path", StringArgumentType.string()).suggests((context, b) -> SharedSuggestionProvider.suggest(ConfigManager.getClientPaths(), b)).executes(context -> {
            String path = context.getArgument("path", String.class);
            if (!ConfigManager.reload(path, null)) {
                context.getSource().arch$getPlayer().displayClientMessage(Component.literal("Failed to reload: ").withStyle(ChatFormatting.RED).append(Component.literal("[" + path + "]")), true);
                return 0;
            }

            context.getSource().arch$getPlayer().displayClientMessage(Component.literal("[" + path + "]").withStyle(ChatFormatting.GRAY).append(Component.literal(" reloaded successfully!").withStyle(ChatFormatting.WHITE)), true);
            return Command.SINGLE_SUCCESS;
        })))));
    }
}
