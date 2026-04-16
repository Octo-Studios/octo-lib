package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.animation.Tween;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.EaseType;
import it.hurts.shatterbyte.shatterlib.client.animation.easing.TransitionType;
import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.Mth;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ToggleWidget extends AbstractEntryWidget<Boolean> {
    Tween buttonTween = Tween.create();

    @Getter
    @Setter
    float progress = 0f;

    public ToggleWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, Boolean defaultValue, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 20, 14);
        if (this.getValue()) {
            progress = 1f;
        }
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        UIElements.TOGGLE_DISABLED.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY() + 3);
        if (this.progress > 0f) {
            guiGraphics.enableScissor(this.getX(), this.getY(), Mth.ceil(this.getX() + progress * 10), this.getY() + this.height + 1);
            UIElements.TOGGLE_ENABLED.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY() + 3);
            guiGraphics.disableScissor();
        }

        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(progress * 10f, 2);
        UIElements.TOGGLE_THINGY.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY());
        guiGraphics.pose().popMatrix();
    }

    private void animateToggle() {
        buttonTween.stop();
        buttonTween = Tween.create();
        buttonTween.tweenMethod(this::setProgress, this.getProgress(), this.getValue() ? 1f : 0f, this.getValue() ? (1 - this.progress) / 3f : this.progress / 3f)
                .setEaseType(EaseType.EASE_OUT)
                .setTransitionType(TransitionType.EXPO);
        buttonTween.start();
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean isDoubleClick) {
        super.onClick(event, isDoubleClick);
        this.setValue(!this.getValue());
        this.animateToggle();
    }

    @Override
    public void resetValue() {
        super.resetValue();
        this.animateToggle();
    }
}
