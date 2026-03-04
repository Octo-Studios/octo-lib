package it.hurts.octostudios.octolib.module.post_effect.instances;

import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.module.chromatic_aberration.ChromaticAberrationManager;
import it.hurts.octostudios.octolib.module.post_effect.PostEffect;
import it.hurts.octostudios.octolib.module.post_effect.RenderStage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

public class ChromaticAberrationPostEffect extends PostEffect {
    private static final Minecraft MC = Minecraft.getInstance();

    @Override
    public void construct(PostChain postChain) {
        var player = MC.player;

        if (player == null)
            return;

        var totalStrength = 0F;

        for (var chromaticAberration : ChromaticAberrationManager.CHROMATIC_ABERRATIONS.values()) {
            var s = chromaticAberration.getStrength(player);

            if (s > 0F)
                totalStrength += s;
        }

        postChain.setUniform("Strength", totalStrength);
    }

    @Override
    public boolean shouldRender() {
        return !ChromaticAberrationManager.CHROMATIC_ABERRATIONS.isEmpty();
    }

    @Override
    public ResourceLocation getPath() {
        return ResourceLocation.fromNamespaceAndPath(OctoLib.MODID, "shaders/post/chromatic_aberration.json");
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.LEVEL;
    }
}