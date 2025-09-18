package it.hurts.shatterbyte.shatterlib.module.particle;

public interface RenderBuffer<P extends RenderProvider<P, B>, B extends RenderBuffer<P, B>> {
    
    void tick(P provider);

}
