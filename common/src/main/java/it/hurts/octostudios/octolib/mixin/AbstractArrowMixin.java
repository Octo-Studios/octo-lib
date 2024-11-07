package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.modules.particles.trail.OctoRenderManager;
import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.projectile.AbstractArrow;
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
public abstract class AbstractArrowMixin extends Entity {
    public AbstractArrowMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(EntityType<? extends Bat> entityType, Level level, CallbackInfo ci) {
        if (!level.isClientSide)
            return;

        OctoRenderManager.registerTrail((TrailProvider) this);
    }

    public Vec3 i$getRenderPosition(float f) {
        return getPosition(f);
    }

    public int i$getMaxSize() {
        return 12;
    }

    public boolean i$isAlive() {
        return super.isAlive();
    }

    public int i$getFadeInColor() {
        return 0xFFFFFFAA;
    }

    public int i$getFadeOutColor() {
        return 0xFFFFFFAA;
    }

    public int i$getBufferTickInterval() {
        return 2;
    }

    public double i$getRadius() {
        return 0.2;
    }
}