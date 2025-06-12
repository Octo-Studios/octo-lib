package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.module.particle.OctoRenderManager;
import it.hurts.octostudios.octolib.module.particle.trail.EntityTrailProvider;
import it.hurts.octostudios.octolib.module.particle.trail.EntityTrailRegistry;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectTrail(EntityType entityType, Level level, CallbackInfo ci) {
        if (level.isClientSide) {
            EntityTrailProvider<Entity> trail = EntityTrailRegistry.getTrailProvider((Entity) (Object) this);
            if (trail != null) {
                OctoRenderManager.registerProvider(trail);
            }
        }
    }
}
