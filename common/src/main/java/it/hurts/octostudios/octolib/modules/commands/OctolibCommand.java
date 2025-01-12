package it.hurts.octostudios.octolib.modules.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

public class OctolibCommand {
    
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection commandSelection) {
        dispatcher.register(Commands.literal("octolib").requires(s -> s.hasPermission(2))
                .then(Commands.literal("config")
                        .then(Commands.literal("reload")
                                .then(Commands.literal("all")
                                        .executes(conComponent -> {
                                            int counter = 0;
                                            boolean isAdmin = conComponent.getSource().hasPermission(4);
                                            
                                            for (var path : ConfigManager.getAllPaths()) {
                                                try {
                                                    if (ConfigManager.isServerConfig(path)) {
                                                        if (isAdmin) {
                                                            ConfigManager.reload(path);
                                                            ConfigManager.syncConfig(path, conComponent.getSource().getServer());
                                                        } else
                                                            conComponent.getSource().sendFailure(
                                                                    Component.literal("You have not permission to reload config."));
                                                    } else {
                                                        ConfigManager.reload(path);
                                                    }
                                                    counter++;
                                                } catch (RuntimeException e) {
                                                    e.printStackTrace();
                                                    conComponent.getSource().sendFailure(Component.literal("Error occurs while reload config by path ")
                                                            .append(Component.literal("\"" + path + "\"")));
                                                    
                                                    return 0;
                                                }
                                            }
    
                                            conComponent.getSource().sendSystemMessage(Component.literal(counter + " configs reload successfully"));
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(Commands.argument("path", StringArgumentType.string())
                                        .suggests((c, b) -> SharedSuggestionProvider.suggest(ConfigManager.getAllPaths(), b))
                                        .executes(c -> {
                                            var path = StringArgumentType.getString(c, "path");
                                            boolean isAdmin = c.getSource().hasPermission(4);
                                            
                                            if (!ConfigManager.getAllPaths().contains(path)) {
                                                c.getSource().sendFailure(Component.literal("Config by path \"" + path + "\" does not exist"));
                                                return 0;
                                            }
                                            
                                            try {

                                                if (ConfigManager.isServerConfig(path)) {
                                                    if (isAdmin) {
                                                        ConfigManager.reload(path);
                                                        ConfigManager.syncConfig(path, c.getSource().getServer());
                                                    } else
                                                        c.getSource().sendFailure(
                                                                Component.literal("You have not permission to reload config."));
                                                } else {
                                                    ConfigManager.reload(path);
                                                }

                                                c.getSource().sendSystemMessage(Component.literal("Config with path \"" + path + "\" has been reloaded successfully"));
                                            } catch (RuntimeException e) {
                                                e.printStackTrace();
                                                c.getSource().sendFailure(Component.literal("Error occurs while reloading config by path ")
                                                        .append(Component.literal("\"" + path + "\"")));
                                                
                                                return 0;
                                            }
    
                                            return Command.SINGLE_SUCCESS;
                                        })))
                )
        );
    }
    
}
