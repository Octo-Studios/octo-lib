package it.hurts.octostudios.octolib.module.post_effect;

import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

public abstract class PostEffect {
    public void construct(PostChain postChain) {

    }

    public boolean shouldRender() {
        return true;
    }

    public abstract ResourceLocation getPath();

    public RenderStage getStage() {
        return RenderStage.SCREEN;
    }
}