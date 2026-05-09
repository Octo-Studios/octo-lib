package it.hurts.shatterbyte.byteapi.mixin;

import it.hurts.shatterbyte.byteapi.module.particle.ShatterRenderManager;
import it.hurts.shatterbyte.byteapi.module.particle.trail.EntityTrailProvider;
import it.hurts.shatterbyte.byteapi.module.particle.trail.EntityTrailRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "addEntity", at = @At("TAIL"))
    private void injectTrail(Entity entity, CallbackInfo ci) {
        EntityTrailProvider<Entity> trail = EntityTrailRegistry.getTrailProvider(entity);
        if (trail != null) {
            ShatterRenderManager.registerProvider(trail);
        }
    }
}
