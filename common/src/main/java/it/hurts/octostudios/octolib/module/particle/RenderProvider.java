package it.hurts.octostudios.octolib.module.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.Vec3;

public interface RenderProvider<P extends RenderProvider<P, B>, B extends RenderBuffer<P, B>> {
    
    Vec3 getRenderPosition(float partialTick);
    
    void render(float pTicks, PoseStack poseStack, VertexConsumer consumer);
    
    default double getRenderDistance() {
        return 64;
    };
    
    boolean shouldRender(B buffer);
    
    int getUpdateFrequency();
    
    B createBuffer();
    
}
