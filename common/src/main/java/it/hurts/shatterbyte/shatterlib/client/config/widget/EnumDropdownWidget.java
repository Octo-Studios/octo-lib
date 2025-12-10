package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumDropdownWidget<E extends Enum<E>> extends AbstractEntryWidget<E> {
    private final List<E> values;
    private boolean open = false;
    private int hoveredIndex = -1;

    public EnumDropdownWidget(ShatterConfig config, FieldWidget parent, E defaultValue, Supplier<E> getter, Consumer<E> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 200, 18);

        Class<E> enumClass = defaultValue.getDeclaringClass();
        this.values = Arrays.asList(enumClass.getEnumConstants());
        if (this.values.isEmpty()) {
            throw new IllegalArgumentException("enum has no constants: " + enumClass);
        }
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = this.getX();
        int y = this.getY();
        int w = this.getWidth();
        int h = 18;

        // background box
        guiGraphics.fill(x, y, x + w, y + h, 0xFF2B2B2B);

        // draw selected value text
        E current = getValue();
        String label = current == null ? "<null>" : convertFromCamelCase(current.name());
        guiGraphics.drawString(Minecraft.getInstance().font, label, x + 6, y + (h - 8) / 2, 0xFFFFFFFF, true);

        int arrowW = 10;
        int ax = x + w - arrowW - 6;
        int ay = y + (h / 2) - 2;
        guiGraphics.fill(ax, ay, ax + arrowW, ay + 4, 0xFF7A7A7A);

        if (isMouseOver(mouseX, mouseY)) {
            guiGraphics.fill(x, y, x + w, y + h, 0x20FFFFFF);
        }

        if (open) {
            Rectangle r = getPopupBounds();
            int listX = r.x;
            int listY = r.y;
            int listW = r.width;
            int optionH = this.getHeight();
            int maxToShow = values.size();

            guiGraphics.fill(listX, listY, listX + listW, listY + optionH * maxToShow, 0xFF1E1E1E);

            for (int i = 0; i < values.size(); i++) {
                int oy = listY + i * optionH;
                if (mouseX >= listX && mouseX < listX + listW && mouseY >= oy && mouseY < oy + optionH && i == hoveredIndex) {
                    guiGraphics.fill(listX, oy, listX + listW, oy + optionH, 0x40FFFFFF);
                }
                String opt = convertFromCamelCase(values.get(i).name());
                guiGraphics.drawString(Minecraft.getInstance().font, opt, listX + 6, oy + (optionH - 8) / 2, 0xFFFFFFFF, true);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (isInside(event.x(), event.y())) {
            open = !open;
            return true;
        }

        if (open) {
            Rectangle r = getPopupBounds();
            if (event.x() >= r.x && event.x() < r.x + r.width && event.y() >= r.y && event.y() < r.y + r.height) {
                int optionH = this.getHeight();
                int idx = (int) ((event.y() - r.y) / optionH);
                idx = Math.max(0, Math.min(values.size() - 1, idx));
                E chosen = values.get(idx);
                setValue(chosen);
                open = false;
                return true;
            } else {
                open = false;
                return false;
            }
        }

        return false;
    }

    private boolean isInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
    }

    private Rectangle getPopupBounds() {
        int x = this.getX();
        int y = this.getY() + this.getHeight();
        int w = this.getWidth();
        int h = this.getHeight() * values.size();

        // clamp to screen so it doesn't go off the visible area (optional, but helpful)
        Minecraft mc = Minecraft.getInstance();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        // if popup would run off bottom, try to open upward instead
        if (y + h > screenH) {
            int altY = this.getY() - h;
            if (altY >= 0) {
                y = altY;
            } else {
                // clamp height if both up and down overflow
                h = Math.max(0, screenH - 4); // small padding
                if (y + h > screenH) h = screenH - y;
            }
        }

        // horizontal clamp (rare)
        if (x + w > screenW) {
            x = Math.max(0, screenW - w);
        }

        return new Rectangle(x, y, w, h);
    }

    private boolean isInsidePopup(double mouseX, double mouseY) {
        Rectangle r = getPopupBounds();
        return mouseX >= r.x && mouseX < r.x + r.width && mouseY >= r.y && mouseY < r.y + r.height;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (super.isMouseOver(mouseX, mouseY)) {
            return true;
        }

        return open && isInsidePopup(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (open) {
            return true;
        }
        return false;
    }
}