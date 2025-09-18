package it.hurts.shatterbyte.shatterlib.client.particle;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class ParticleSystem {
    protected static List<UIParticle> GUI_PARTICLES = new ArrayList<>();
    protected static Map<Screen, List<UIParticle>> SCREEN_PARTICLES = new WeakHashMap<>();

    public static void tick() {
        GUI_PARTICLES.forEach(UIParticle::tick);
        SCREEN_PARTICLES.values().forEach(uiParticles -> uiParticles.forEach(UIParticle::tick));

        GUI_PARTICLES.removeIf(UIParticle::isExpired);
        SCREEN_PARTICLES.values().forEach(uiParticles -> uiParticles.removeIf(UIParticle::isExpired));
        SCREEN_PARTICLES.values().removeIf(List::isEmpty);
    }

    public static void renderScreenParticles(Screen screen, GuiGraphics guiGraphics, float partialTicks) {
        if (SCREEN_PARTICLES.containsKey(screen)) {
            SCREEN_PARTICLES.get(screen).forEach(uiParticle -> uiParticle.render(guiGraphics, partialTicks));
        }
    }

    public static void renderGuiParticles(GuiGraphics guiGraphics, float partialTicks) {
        GUI_PARTICLES.forEach(uiParticle -> uiParticle.render(guiGraphics, partialTicks));
    }
}
