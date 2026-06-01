package it.hurts.shatterbyte.shatterlib.module.post_effect;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;

public abstract class PostEffect {
    public void construct(PostChain postChain) {

    }

    public boolean shouldRender() {
        return true;
    }

    public abstract Identifier getPath();

    public RenderStage getStage() {
        return RenderStage.SCREEN;
    }
}
