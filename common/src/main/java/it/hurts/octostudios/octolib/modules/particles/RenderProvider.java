package it.hurts.octostudios.octolib.modules.particles;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public interface  RenderProvider<P extends RenderProvider<P, B>, B extends RenderBuffer<P, B>> {

    Vec3 getRenderPosition(float partialTick);

    void render(float pTicks, PoseStack poseStack, MultiBufferSource bufferSourceList);

    default double getRenderDistance() {
        return 64;
    };

    boolean shouldRender(B buffer);

    int getUpdateFrequency();

    B createBuffer();

}
