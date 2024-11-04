package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.modules.particles.trail.TrailManager;
import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Entity {
    public AbstractArrowMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("RETURN"), method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V")
    public void init(EntityType<? extends AbstractArrow> entityType, Level level, CallbackInfo ci) {
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
        return 5;
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
        return 1;
    }

    public double i$width() {
        return 0.1;
    }
}