package it.hurts.octostudios.octolib.modules.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class OctolibCommand {
    
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(CommandManager.literal("octolib").requires(s -> s.hasPermissionLevel(2))
                .then(CommandManager.literal("config")
                        .then(CommandManager.literal("reload")
                                .then(CommandManager.literal("all")
                                        .executes(context -> {
                                            int counter = 0;
                                            
                                            for (var path : ConfigManager.getAllPaths()) {
                                                try {
                                                    ConfigManager.reload(path);
                                                    counter++;
                                                } catch (RuntimeException e) {
                                                    e.printStackTrace();
                                                    context.getSource().sendError(Text.literal("Error occurs while reload config by path ")
                                                            .append(Text.literal("\"" + path + "\"")));
                                                    
                                                    return 0;
                                                }
                                            }
    
                                            context.getSource().sendMessage(Text.literal(counter + " configs reload successfully"));
                                            return Command.SINGLE_SUCCESS;
                                        }))
                                .then(CommandManager.argument("path", StringArgumentType.string())
                                        .suggests((c, b) -> CommandSource.suggestMatching(ConfigManager.getAllPaths(), b))
                                        .executes(c -> {
                                            var path = StringArgumentType.getString(c, "path");
                                            
                                            if (!ConfigManager.getAllPaths().contains(path)) {
                                                c.getSource().sendError(Text.literal("Config by path \"" + path + "\" does not exist"));
                                                return 0;
                                            }
                                            
                                            try {
                                                ConfigManager.reload(path);
                                            } catch (RuntimeException e) {
                                                e.printStackTrace();
                                                c.getSource().sendError(Text.literal("Error occurs while reloading config by path ")
                                                        .append(Text.literal("\"" + path + "\"")));
                                                
                                                return 0;
                                            }
    
                                            return Command.SINGLE_SUCCESS;
                                        })))
                )
        );
    }
    
}
