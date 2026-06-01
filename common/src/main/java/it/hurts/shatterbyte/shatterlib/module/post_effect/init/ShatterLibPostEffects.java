package it.hurts.shatterbyte.shatterlib.module.post_effect.init;

import it.hurts.shatterbyte.shatterlib.module.post_effect.PostEffect;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ShatterLibPostEffects {
    private static final Map<Identifier, Supplier<PostEffect>> POST_EFFECTS_REGISTRY = new ConcurrentHashMap<>();
    private static final Map<Identifier, PostEffect> POST_EFFECTS = new HashMap<>();

    public static void register(Supplier<PostEffect> postEffect) {
        POST_EFFECTS_REGISTRY.put(postEffect.get().getPath(), postEffect);
    }

    public static Map<Identifier, PostEffect> getPostEffects() {
        return POST_EFFECTS;
    }

    public static void init() {
        for (Map.Entry<Identifier, Supplier<PostEffect>> entry : POST_EFFECTS_REGISTRY.entrySet())
            POST_EFFECTS.put(entry.getKey(), entry.getValue().get());
    }
}
