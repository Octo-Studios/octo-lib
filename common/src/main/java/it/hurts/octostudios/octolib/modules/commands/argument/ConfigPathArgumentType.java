package it.hurts.octostudios.octolib.modules.commands.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import it.hurts.octostudios.octolib.modules.config.impl.OctoConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class ConfigPathArgumentType implements ArgumentType<ConfigPathArgumentType.OctoConfigArgument> {
    
    private static final Collection<String> EXAMPLES = Arrays.asList("mod/config1", "mod/dir/config2");
    private static final DynamicCommandExceptionType INVALID_PATH_EXCEPTION = new DynamicCommandExceptionType((path) ->
            Component.translatable("argument.config.path.invalid", path)); // The translatable component param should probably be properly escaped
    
    public ConfigPathArgumentType() {
    }
    
    public static ConfigPathArgumentType.OctoConfigArgument getConfigArgument(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, OctoConfigArgument.class);
    }
    
    public ConfigPathArgumentType.OctoConfigArgument parse(StringReader stringReader) throws CommandSyntaxException {
        int i = stringReader.getCursor();
        String string = stringReader.readUnquotedString();
        OctoConfig octoConfig = ConfigManager.getConfig(string);
        if (octoConfig == null) {
            stringReader.setCursor(i);
            throw INVALID_PATH_EXCEPTION.createWithContext(stringReader, string);
        } else {
            return new OctoConfigArgument(string, octoConfig);
        }
    }
    
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(ConfigManager.getAllPaths(), builder);
    }
    
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
    
    public static class OctoConfigArgument {
        
        String path;
        OctoConfig config;
    
        public OctoConfigArgument(String path, OctoConfig config) {
            this.path = path;
            this.config = config;
        }
    
        public String getPath() {
            return path;
        }
    
        public OctoConfig getConfig() {
            return config;
        }
    
    }
    
}
