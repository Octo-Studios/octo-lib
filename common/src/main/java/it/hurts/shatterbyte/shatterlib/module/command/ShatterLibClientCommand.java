package it.hurts.shatterbyte.shatterlib.module.command;

import com.mojang.brigadier.Command;
import it.hurts.shatterbyte.shatterlib.module.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class ShatterLibClientCommand {
    public static int reloadAll(java.util.function.Consumer<Component> feedback) {
        int counter = 0;

        for (String path : ConfigManager.getClientPaths()) {
            if (!ConfigManager.reload(path, null)) {
                feedback.accept(Component.literal("Failed to reload: ").withStyle(ChatFormatting.RED).append(Component.literal("[" + path + "]")));
                continue;
            }

            counter++;
        }

        feedback.accept(Component.literal(counter + " configs reloaded successfully!"));
        return Command.SINGLE_SUCCESS;
    }

    public static int reloadOne(String path, java.util.function.Consumer<Component> feedback) {
        if (!ConfigManager.reload(path, null)) {
            feedback.accept(Component.literal("Failed to reload: ").withStyle(ChatFormatting.RED).append(Component.literal("[" + path + "]")));
            return 0;
        }

        feedback.accept(Component.literal("[" + path + "]").withStyle(ChatFormatting.GRAY).append(Component.literal(" reloaded successfully!").withStyle(ChatFormatting.WHITE)));
        return Command.SINGLE_SUCCESS;
    }
}
