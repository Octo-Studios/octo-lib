package it.hurts.octostudios.octolib.module.particle;

public interface RenderBuffer<P extends RenderProvider<P, B>, B extends RenderBuffer<P, B>> {
    
    void tick(P provider);

}
