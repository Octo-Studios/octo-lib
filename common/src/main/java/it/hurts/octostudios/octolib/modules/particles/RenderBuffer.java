package it.hurts.octostudios.octolib.modules.particles;

public interface RenderBuffer<P extends RenderProvider<P, B>, B extends RenderBuffer<P, B>> {

    void tick(P provider);

}
