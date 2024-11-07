package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.modules.particles.trail.OctoRenderManager;
import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Implements(@Interface(iface = TrailProvider.class, prefix = "i$"))
@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin extends Entity {
    
    public ExperienceOrbMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    
    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V")
    public void init(EntityType<? extends ExperienceOrb> entityType, Level level, CallbackInfo ci) {
        if (!level.isClientSide)
            return;
        
        OctoRenderManager.registerTrail((TrailProvider) this);
    }
    
    public Vec3 i$getRenderPosition(float f) {
        var yOff = 0.175F;
        
        return getPosition(f).add(0, yOff, 0);
    }
    
    public int i$getMaxSize() {
        return 5;
    }
    
    public boolean i$isAlive() {
        return super.isAlive();
    }
    
    public int i$getFadeInColor() {
        return 0x8066ff00;
    }
    
    public int i$getFadeOutColor() {
        return 0x80d9ff00;
    }
    
    public int i$getBufferTickInterval() {
        return 1;
    }
    
    public double i$getRadius() {
        return 0.075;
    }
    
    public double i$getMinDistanceDelta() {
        return 0.003;
    }
    
}