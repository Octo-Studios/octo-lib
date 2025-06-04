package it.hurts.octostudios.octolib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TestScreen extends Screen {
    private int ticker;
    private boolean down;

    public TestScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {
        int x = 8;
        int y = 16;
        for (Easing easing : Easing.values()) {
            this.addRenderableWidget(new TestWidget(x, y, easing));
            y += 30;

            if (y + 30 > this.height) {
                x += 120;
                y = 16;
            }
        }
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                String.valueOf(ticker),
                this.width - Minecraft.getInstance().font.width(String.valueOf(ticker)) - 4,
                4, 0xffffffff, true);
        guiGraphics.drawString(
                Minecraft.getInstance().font,
                String.valueOf(f),
                this.width - Minecraft.getInstance().font.width(String.valueOf(f)) - 4,
                this.height - 10, 0xffffffff, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean result = super.mouseClicked(mouseX, mouseY, button);
        if (!result && button == 1) {
            for (GuiEventListener g : this.children()) {
                if (g instanceof TestWidget widget) {
                    widget.onClick(mouseX, mouseY);
                }
            }
        }
        return result;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void onClose() {
        for (GuiEventListener g : this.children()) {
            if (g instanceof TestWidget widget && widget.animator != null) {
                widget.animator.stop();
            }
        }

        super.onClose();
    }
}
