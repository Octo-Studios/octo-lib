package it.hurts.octostudios.octolib.mixin;

import it.hurts.octostudios.octolib.modules.particles.OctoRenderManager;
import it.hurts.octostudios.octolib.modules.particles.trail.TrailProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ItemEntity.class)
public class EntityMixin implements TrailProvider {
    @Inject(at = @At("RETURN"), method = "<init>*")
    public void init(EntityType<? extends Bat> entityType, Level level, CallbackInfo ci) {
        if (!level.isClientSide)
            return;

        OctoRenderManager.registerProvider(this);
    }

    @Override
    public List<Vec3> getTrailRenderPositions(List<Vec3> points, float partialTicks) {
        if (points.isEmpty())
            return points;

        var entity = (ItemEntity) (Object) this;
        var index = 0;

        points.set(index, points.get(index).add(0, Mth.sin((entity.getAge()) / 10F + entity.bobOffs), 0F));

        return points;
    }

    @Override
    public Vec3 getTrailPosition(float f) {
        var entity = (Entity) (Object) this;

        return entity.tickCount > 1 ? entity.getPosition(f) : entity.position();
    }

    @Override
    public int getTrailMaxLength() {
        return 15;
    }

    @Override
    public boolean isTrailAlive() {
        var entity = (Entity) (Object) this;

        return entity.isAlive();
    }

    @Override
    public int getTrailFadeInColor() {
        return 0xFFFFFFAA;
    }

    @Override
    public int getTrailFadeOutColor() {
        return 0xFFFFFFAA;
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public double getTrailScale() {
        return 0.25;
    }
}