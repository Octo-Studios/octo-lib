package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

import java.awt.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumDropdownWidget<E extends Enum<E>> extends AbstractEntryWidget<E> {
    private final List<E> values;
    private boolean open = false;
    private boolean closeAfterRelease = false;
    private int hoveredIndex = -1;

    public EnumDropdownWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, E defaultValue, Supplier<E> getter, Consumer<E> setter) {
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
        int h = this.getHeight();

        guiGraphics.fill(x, y, x + w, y + h, 0xff1f1e23);

        if (isMouseOver(mouseX, mouseY)) {
            guiGraphics.fill(x, y, x + w, y + h, 0x20FFFFFF);
        }

        // draw selected value text
        E current = getValue();
        String label = current == null ? "<null>" : convertFromCamelCase(current.name());
        guiGraphics.drawString(Minecraft.getInstance().font, label, x + 6, y + (h - 8) / 2, 0xFFFFFFFF, true);

        int arrowW = 9;
        int ax = x + w - arrowW - 6;
        int ay = y + (h / 2) - 2;
        (open ? UIElements.ICON_UP : UIElements.ICON_DOWN).render(guiGraphics, RenderPipelines.GUI_TEXTURED, ax, ay-3);

        UIElements.FRAME.render(guiGraphics, RenderPipelines.GUI_TEXTURED, x - 1, y - 1, w + 2, h + 2);

        if (!open) {
            hoveredIndex = -1;
        }

        if (open) {
            Rectangle r = getPopupBounds();
            int listX = r.x;
            int listY = r.y;
            int listW = r.width;
            int optionH = this.getHeight();
            int maxToShow = values.size();
            hoveredIndex = getHoveredIndex(mouseX, mouseY);

            for (int i = 0; i < maxToShow; i++) {
                int oy = listY + i * optionH;
                guiGraphics.fill(listX, oy, listX + listW, oy + optionH, i % 2 == 0 ? 0xff131418 : 0xff1f1e23);
                if (hoveredIndex == i) {
                    guiGraphics.fill(listX, oy, listX + listW, oy + optionH, 0x40FFFFFF);
                }
                String opt = convertFromCamelCase(values.get(i).name());
                guiGraphics.drawString(Minecraft.getInstance().font, opt, listX + 6, oy + (optionH - 8) / 2, 0xFFFFFFFF, true);
            }

            UIElements.FRAME.render(guiGraphics, RenderPipelines.GUI_TEXTURED, listX - 1, listY - 1, listW + 2, optionH * maxToShow + 2);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (open) {
            Rectangle r = getPopupBounds();
            if (event.x() >= r.x && event.x() < r.x + r.width && event.y() >= r.y && event.y() <= r.y + r.height) {
                int optionH = this.getHeight();
                int idx = (int) ((event.y() - r.y) / optionH);
                idx = Math.max(0, Math.min(values.size() - 1, idx));
                E chosen = values.get(idx);
                setValue(chosen);
                closeAfterRelease = true;
                return true;
            }

            if (isInside(event.x(), event.y())) {
                open = false;
                closeAfterRelease = false;
                return true;
            }

            open = false;
            closeAfterRelease = false;
            return false;
        }

        if (isInside(event.x(), event.y())) {
            open = true;
            closeAfterRelease = false;
            if (this.getParent() != null) {
                this.getParent().moveToTheTop();
            }
            return true;
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

        Minecraft mc = Minecraft.getInstance();
        int screenW = mc.getWindow().getGuiScaledWidth();
        int screenH = mc.getWindow().getGuiScaledHeight();

        if (y + h > screenH) {
            int altY = this.getY() - h;
            if (altY >= 0) {
                y = altY;
            } else {
                y = Math.max(0, screenH - h);
            }
        }

        if (y + h > screenH) {
            y = Math.max(0, screenH - h);
        }
        if (x + w > screenW) {
            x = Math.max(0, screenW - w);
        }

        return new Rectangle(x, y, w, h);
    }

    private int getHoveredIndex(int mouseX, int mouseY) {
        Rectangle r = getPopupBounds();
        if (mouseX < r.x || mouseX >= r.x + r.width || mouseY < r.y || mouseY > r.y + r.height) {
            return -1;
        }

        int optionH = this.getHeight();
        int idx = (mouseY - r.y) / optionH;
        if (idx < 0) {
            return -1;
        }

        return Math.min(values.size() - 1, idx);
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        if (!focused) {
            this.open = false;
            this.closeAfterRelease = false;
        }
    }

    private boolean isInsidePopup(double mouseX, double mouseY) {
        Rectangle r = getPopupBounds();
        return mouseX >= r.x && mouseX < r.x + r.width && mouseY >= r.y && mouseY <= r.y + r.height;
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
        if (closeAfterRelease) {
            closeAfterRelease = false;
            open = false;
            return true;
        }

        if (open) {
            return true;
        }

        return false;
    }
}
