package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.modules.particles.trail.TrailManager;
import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Implements(@Interface(iface = TrailProvider.class, prefix = "i$"))
@Mixin(Bat.class)
public abstract class XPTestMixin extends Entity {
    
    public XPTestMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    
    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(EntityType entityType, Level level, CallbackInfo ci) {
        if (!level.isClientSide)
            return;
        
        TrailManager.registerTrail((TrailProvider) this);
    }
    
    public Vec3 i$getPointPosition(float f) {
        var position = position();
        return new Vec3(
                Mth.lerp(f, xo, position.x),
                Mth.lerp(f, yo, position.y),
                Mth.lerp(f, zo, position.z)
        );
    }
    
    public int i$maxSize() {
        return 20;
    }
    
    public boolean i$isAlive() {
        return super.isAlive();
    }
    
    public int i$fadeIn() {
        return 0xFFFFFFAA;
    }
    
    public int i$fadeOut() {
        return 0xFFFFFFAA;
    }
    
    public int i$frequency() {
        return 4;
    }
    
    public double i$width() {
        return 0.1;
    }
    
}
