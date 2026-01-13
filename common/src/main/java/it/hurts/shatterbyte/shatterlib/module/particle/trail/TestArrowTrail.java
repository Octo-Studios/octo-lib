package it.hurts.shatterbyte.shatterlib.module.particle.trail;


import net.minecraft.world.entity.projectile.arrow.Arrow;

public class TestArrowTrail extends EntityTrailProvider<Arrow> {
    public TestArrowTrail(Arrow entity) {
        super(entity);
    }

    @Override
    public int getTrailUpdateFrequency() {
        return 1;
    }

    @Override
    public int getTrailMaxLength() {
        return 10;
    }

    @Override
    public int getTrailFadeInColor() {
        return 0x000000FF;
    }

    @Override
    public int getTrailFadeOutColor() {
        return 0xFFFF00FF;
    }

    @Override
    public double getTrailScale() {
        return 0.15;
    }
}
