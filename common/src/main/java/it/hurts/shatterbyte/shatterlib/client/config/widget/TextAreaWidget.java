package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.Minecart;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class TextAreaWidget extends AbstractEntryWidget<String> {
    Font font = Minecraft.getInstance().font;
    MyTextAreaWidget box;

    public TextAreaWidget(ShatterConfig config, FieldWidget parent, String defaultValue, Supplier<String> getter, Consumer<String> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 256, 14);
        this.box = new MyTextAreaWidget(font, this.width, this.height, this);
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        box.render(guiGraphics, mouseX, mouseY, partialTick);
        //guiGraphics.drawString(Minecraft.getInstance().font, this.getValue(), this.getX(), this.getY(), 0xffcccccc, true);
    }

    @Override
    public int getWidth() {
        return 256;
    }

    @Override
    public void playDownSound(SoundManager handler) {}

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (super.mouseClicked(event, isDoubleClick)) {
            return box.mouseClicked(event, isDoubleClick);
        }

        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (super.mouseReleased(event)) {
            return box.mouseReleased(event);
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) {
            return box.mouseScrolled(mouseX,mouseY,scrollX,scrollY);
        }

        return false;
    }
}
