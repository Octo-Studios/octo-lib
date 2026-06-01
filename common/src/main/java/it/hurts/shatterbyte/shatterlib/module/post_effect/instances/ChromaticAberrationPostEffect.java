package it.hurts.shatterbyte.shatterlib.module.post_effect.instances;

import it.hurts.shatterbyte.shatterlib.ShatterLib;
import it.hurts.shatterbyte.shatterlib.module.chromatic_aberration.ChromaticAberrationManager;
import it.hurts.shatterbyte.shatterlib.module.post_effect.PostEffect;
import it.hurts.shatterbyte.shatterlib.module.post_effect.PostUniforms;
import it.hurts.shatterbyte.shatterlib.module.post_effect.RenderStage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.Identifier;

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

        PostUniforms.setFloats(postChain, "ChromaticAberrationConfig", totalStrength);
    }

    @Override
    public boolean shouldRender() {
        return !ChromaticAberrationManager.CHROMATIC_ABERRATIONS.isEmpty();
    }

    @Override
    public Identifier getPath() {
        return Identifier.fromNamespaceAndPath(ShatterLib.MOD_ID, "chromatic_aberration");
    }

    @Override
    public RenderStage getStage() {
        return RenderStage.LEVEL;
    }
}
