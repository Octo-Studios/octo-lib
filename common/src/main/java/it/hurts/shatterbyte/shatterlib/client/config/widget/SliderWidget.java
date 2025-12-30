package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.animation.Tween;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.EaseType;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Range;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;

import java.lang.annotation.Annotation;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SliderWidget<N extends Number> extends AbstractEntryWidget<N> {
    private double min = 0;
    private double max = 100;
    private double step = 0;
    private final Class<?> valueClass;

    @Setter
    double visualValue;
    Tween tween = Tween.create();

    public SliderWidget(ShatterConfig config, Range range, PathContainerWidget parent, N defaultValue, Supplier<N> getter, Consumer<N> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 225, 8);
        this.valueClass = defaultValue != null ? defaultValue.getClass() : Double.class;
        this.setRange(range.min(), range.max(), range.step());
        this.visualValue = this.getValue() == null ? 0d : numberToDouble(this.getValue());

        //clampCachedToRange();
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

    @Override
    public void setValue(N value) {
        super.setValue(value);
    }

    @Override
    public void resetValue() {
        super.resetValue();
        this.animate(this.getValue());
    }

    private void animate(N newValue) {
        tween.kill();
        tween = Tween.create();
        tween.tweenMethod(this::setVisualValue, visualValue, numberToDouble(newValue), 0.25d)
                .setEaseType(EaseType.EASE_OUT)
                .setTransitionType(TransitionType.EXPO);
        tween.start();
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
        double current = visualValue;
        double pct = (max == min) ? 0.0 : (current - min) / (max - min);
        pct = Math.max(0.0, Math.min(1.0, pct));

        float fillX = (float) (this.getX()+this.getWidth()*pct-1);

        UIElements.SLIDER_EMPTY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY()+2);
        guiGraphics.enableScissor(this.getX(), this.getY(), (int) fillX, this.getY()+this.getHeight());
        UIElements.SLIDER_FULL.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY()+2);
        guiGraphics.disableScissor();

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(fillX, 0);
        UIElements.SLIDER_THINGY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, -2, this.getY());
        guiGraphics.pose().popMatrix();
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        super.onClick(event, isDoubleClick);
        updateFromMouse(event.x());
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        super.onRelease(event);
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double mouseX, double mouseY) {
        super.onDrag(event, mouseX, mouseY);
        updateFromMouse(event.x());
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double mouseX, double mouseY) {
        return super.mouseDragged(event, mouseX, mouseY);
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
        this.animate(this.getValue());
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