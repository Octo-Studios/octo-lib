package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class SliderWidget<N extends Number> extends AbstractEntryWidget<N> {
    private double min = 0;
    private double max = 100;
    private double step = 0;
    private boolean dragging = false;
    private final Class<?> valueClass;

    public SliderWidget(ShatterConfig config, PathContainerWidget parent, N defaultValue, Supplier<N> getter, Consumer<N> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 225, 8);
        this.valueClass = defaultValue != null ? defaultValue.getClass() : Double.class;
        clampCachedToRange();
    }

    public void setRange(double min, double max, double step) {
        if (Double.isFinite(min) && Double.isFinite(max) && min < max) {
            this.min = min;
            this.max = max;
        }
        if (step > 0) {
            this.step = step;
        } else {
            this.step = 0;
        }
        clampCachedToRange();
    }

    private void clampCachedToRange() {
        N current = getValue();
        if (current == null) return;
        double d = numberToDouble(current);
        if (d < min) d = min;
        if (d > max) d = max;
        setValue(convertDoubleToNumber(d));
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        double current = numberToDouble(getValue());
        double pct = (max == min) ? 0.0 : (current - min) / (max - min);
        pct = Math.max(0.0, Math.min(1.0, pct));

        int fillX = (int) (this.getX()+this.getWidth()*pct)-1;

        UIElements.SLIDER_EMPTY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY()+2);
        guiGraphics.enableScissor(this.getX(), this.getY(), fillX, this.getY()+this.getHeight());
        UIElements.SLIDER_FULL.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY()+2);
        guiGraphics.disableScissor();

        UIElements.SLIDER_THINGY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, fillX-2, this.getY());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        if (isInside(event.x(), event.y())) {
            updateFromMouse(event.x());
            dragging = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        if (dragging) {
            updateFromMouse(event.x());
            return true;
        }
        return false;
    }

    private boolean isInside(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth() && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
    }

    private void updateFromMouse(double mouseX) {
        double relative = (mouseX - this.getX()) / (double) this.getWidth();
        relative = Math.max(0.0, Math.min(1.0, relative));
        double value = min + relative * (max - min);

        if (step > 0) {
            value = Math.round(value / step) * step;
            // clamp to min/max after snapping
            if (value < min) value = min;
            if (value > max) value = max;
        }

        setValue(convertDoubleToNumber(value));
    }

    private double numberToDouble(Number n) {
        if (n == null) return 0.0;
        return n.doubleValue();
    }

    @SuppressWarnings("unchecked")
    private N convertDoubleToNumber(double d) {
        if (valueClass == Integer.class || valueClass == int.class) {
            return (N) Integer.valueOf((int) Math.round(d));
        } else if (valueClass == Long.class || valueClass == long.class) {
            return (N) Long.valueOf(Math.round(d));
        } else if (valueClass == Float.class || valueClass == float.class) {
            return (N) Float.valueOf((float) d);
        } else if (valueClass == Double.class || valueClass == double.class) {
            return (N) Double.valueOf(d);
        } else if (valueClass == Short.class || valueClass == short.class) {
            return (N) Short.valueOf((short) Math.round(d));
        } else if (valueClass == Byte.class || valueClass == byte.class) {
            return (N) Byte.valueOf((byte) Math.round(d));
        } else {
            // fallback to Double
            return (N) Double.valueOf(d);
        }
    }
}