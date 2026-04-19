package it.hurts.shatterbyte.shatterlib.module.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
//import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.Permissions;

import java.util.concurrent.ConcurrentNavigableMap;

public class ShatterLibCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(getBuilder());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> getBuilder() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("shatterlib").requires(s -> s.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("config")
                        .then(Commands.literal("reload")
                                .then(Commands.literal("all")
                                        .executes(context -> {
                                            int counter = 0;
                                            //boolean isAdmin = context.getSource().permissions().hasPermission(Permissions.COMMANDS_OWNER);

                                            for (String path : ConfigManager.getCommonAndServerPaths()) {
                                                if (!ConfigManager.reload(path, context.getSource().getServer())) {
                                                    context.getSource().sendFailure(Component.literal("Failed to reload: ").append(Component.literal("["+path+"]").withStyle(ChatFormatting.GRAY)));
                                                    continue;
                                                }

                                                counter++;
                                            }

                                            context.getSource().sendSystemMessage(Component.literal(counter + " configs reloaded successfully!"));
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(Commands.argument("path", StringArgumentType.string())
                                        .suggests((context, b) -> SharedSuggestionProvider.suggest(ConfigManager.getCommonAndServerPaths().stream().map(string -> '"'+string+'"'), b))
                                        .executes(context -> {
                                            String path = context.getArgument("path", String.class);
                                            if (!ConfigManager.reload(path, context.getSource().getServer())) {
                                                context.getSource().sendFailure(Component.literal("Failed to reload: "+path));
                                                return 0;
                                            }

                                            context.getSource().sendSystemMessage(Component.literal("["+path+"]").withStyle(ChatFormatting.GRAY).append(Component.literal(" reloaded successfully!").withStyle(ChatFormatting.WHITE)));
                                            return Command.SINGLE_SUCCESS;
                                        })))
                );

        if (Platform.isDevelopmentEnvironment()) {
            builder.then(Commands.literal("animatorSystemTestScreen")
                    .executes(component -> {
                        if (component.getSource().getPlayer() == null) {
                            component.getSource().sendFailure(Component.literal("This command should be ran by a player.").withStyle(ChatFormatting.RED));
                        }

                        NetworkManager.sendToPlayer(component.getSource().getPlayer(), new TestScreenPacket());
                        return Command.SINGLE_SUCCESS;
                    }));
        }

        return builder;
    }
}
