package it.hurts.shatterbyte.shatterlib.module.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
//import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import it.hurts.shatterbyte.shatterlib.module.config.network.TestScreenPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class ShatterLibCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(getBuilder());
    }

    private static LiteralArgumentBuilder<CommandSourceStack> getBuilder() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("shatterlib").requires(s -> s.hasPermission(2))
                .then(Commands.literal("config")
                        .then(Commands.literal("reload")
                                .then(Commands.literal("all")
                                        .executes(conComponent -> {
//                                            int counter = 0;
//                                            boolean isAdmin = conComponent.getSource().hasPermission(4);
//
//                                            for (var path : ConfigManager.getAllPaths()) {
//                                                try {
//                                                    if (ConfigManager.isServerConfig(path)) {
//                                                        if (isAdmin) {
//                                                            ConfigManager.reload(path);
//                                                            ConfigManager.syncConfig(path, conComponent.getSource().getServer());
//                                                        } else
//                                                            conComponent.getSource().sendFailure(
//                                                                    Component.literal("You have not permission to reload config."));
//                                                    } else {
//                                                        ConfigManager.reload(path);
//                                                    }
//                                                    counter++;
//                                                } catch (RuntimeException e) {
//                                                    e.printStackTrace();
//                                                    conComponent.getSource().sendFailure(Component.literal("Error occurs while reload config by path ")
//                                                            .append(Component.literal("\"" + path + "\"")));
//
//                                                    return 0;
//                                                }
//                                            }
//
//                                            conComponent.getSource().sendSystemMessage(Component.literal(counter + " configs reload successfully"));
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(Commands.argument("path", StringArgumentType.string())
                                        //.suggests((c, b) -> SharedSuggestionProvider.suggest(ConfigManager.getAllPaths(), b))
                                        .executes(c -> {
//                                            var path = StringArgumentType.getString(c, "path");
//                                            boolean isAdmin = c.getSource().hasPermission(4);
//
//                                            if (!ConfigManager.getAllPaths().contains(path)) {
//                                                c.getSource().sendFailure(Component.literal("Config by path \"" + path + "\" does not exist"));
//                                                return 0;
//                                            }
//
//                                            try {
//
//                                                if (ConfigManager.isServerConfig(path)) {
//                                                    if (isAdmin) {
//                                                        ConfigManager.reload(path);
//                                                        ConfigManager.syncConfig(path, c.getSource().getServer());
//                                                    } else
//                                                        c.getSource().sendFailure(
//                                                                Component.literal("You have not permission to reload config."));
//                                                } else {
//                                                    ConfigManager.reload(path);
//                                                }
//
//                                                c.getSource().sendSystemMessage(Component.literal("Config with path \"" + path + "\" has been reloaded successfully"));
//                                            } catch (RuntimeException e) {
//                                                e.printStackTrace();
//                                                c.getSource().sendFailure(Component.literal("Error occurs while reloading config by path ")
//                                                        .append(Component.literal("\"" + path + "\"")));
//
//                                                return 0;
//                                            }

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
