package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.client.config.UIElements;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.util.RenderUtils;
import lombok.Setter;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.Minecart;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class TextAreaWidget extends AbstractEntryWidget<String> {
    Font font = Minecraft.getInstance().font;
    int cursorPos;

    @Setter
    Predicate<String> predicate = s -> true;

    public TextAreaWidget(ShatterConfig config, Type type, Annotation[] annotations, PathContainerWidget parent, String defaultValue, Supplier<String> getter, Consumer<String> setter) {
        super(config, parent, defaultValue, getter, setter, 0, 0, 200, 14);
    }

    public boolean seek(int where) {
        int oldPos = this.cursorPos;
        this.cursorPos = Math.clamp(where, 0, this.getValue().length());
        return oldPos != this.cursorPos;
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        String value = this.getValue();

        UIElements.TEXT_AREA.render(guiGraphics, RenderPipelines.GUI_TEXTURED, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        guiGraphics.drawString(font, value, this.getX()+4, this.getY()+4, 0xffcccccc, true);

        if (this.isFocused()) {
            String before = value.substring(0, cursorPos);
            guiGraphics.vLine(this.getX() + font.width(before) + 3, this.getY() + 1, this.getY() + height - 2, 0xdd999999);
        }
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        String value = this.getValue();
        String insert = event.codepointAsString();

        // split around cursor
        String before = value.substring(0, cursorPos);
        String after = value.substring(cursorPos);

        String newString = before + insert + after;

        if (!predicate.test(newString)) {
            return false;
        }

        this.setValue(newString);
        this.seek(cursorPos + insert.length());
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isLeft()) {
            if (this.seek(this.cursorPos - 1)) {
                return true;
            }
        }

        if (event.isRight()) {
            if (this.seek(this.cursorPos + 1)) {
                return true;
            }
        }

        // backspace
        if (event.key() == 259) {
            if (cursorPos == 0) {
                return true;
            }

            String value = this.getValue();

            int deleteFrom = value.offsetByCodePoints(cursorPos, -1);

            String before = value.substring(0, deleteFrom);
            String after = value.substring(cursorPos);

            String newString = before + after;

            if (!predicate.test(newString)) {
                return false;
            }

            this.setValue(newString);
            this.seek(deleteFrom);
            return true;
        }

        return false;
    }

    public static Predicate<String> integerPredicate() {
        return s -> s.isEmpty() || s.matches("-?\\d+");
    }

    public static Predicate<String> floatPredicate() {
        return s -> s.isEmpty() || s.matches("-?\\d*(\\.\\d*)?");
    }

    @SuppressWarnings("unchecked")
    public static <N extends Number> N parseNumber(Type type, String s) {
        if (type == int.class || type == Integer.class) {
            return (N) Integer.valueOf(s);
        }
        if (type == float.class || type == Float.class) {
            return (N) Float.valueOf(s);
        }
        if (type == double.class || type == Double.class) {
            return (N) Double.valueOf(s);
        }
        if (type == long.class || type == Long.class) {
            return (N) Long.valueOf(s);
        }
        if (type == short.class || type == Short.class) {
            return (N) Short.valueOf(s);
        }
        if (type == byte.class || type == Byte.class) {
            return (N) Byte.valueOf(s);
        }

        throw new IllegalArgumentException("unsupported number type: " + type);
    }

    public static Predicate<String> numericPredicateFor(Type type) {
        if (type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == short.class || type == Short.class ||
                type == byte.class || type == Byte.class) {
            return TextAreaWidget.integerPredicate();
        }

        return TextAreaWidget.floatPredicate();
    }
}
