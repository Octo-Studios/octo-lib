package it.hurts.shatterbyte.shatterlib.client.config;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractEntryWidget<E> extends AbstractWidget {
    private Component description;
    private E cachedValue;
    private final E defaultValue;

    private final Supplier<E> getter;
    private final Consumer<E> setter;

    public AbstractEntryWidget(E defaultValue, Supplier<E> getter, Consumer<E> setter, int x, int y, int width, int height, String configPath, String fieldName) {
        super(x, y, width, height, Component.translatable(getNameTranslationKey(configPath, fieldName)));
        this.defaultValue = defaultValue;
        this.getter = getter;
        this.setter = setter;

        this.updateCachedValue();
    }

    @Override
    protected final void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        Component message = this.getMessage().copy().append(": ");
        guiGraphics.drawString(font, message, this.getX(), this.getY(), 0xffffffff, true);
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(font.width(message) + this.getX(), this.getY());
        this.renderEntry(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.pose().popMatrix();
    }

    protected abstract void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick);

    public E getValue() {
        if (this.cachedValue == null) {
            this.updateCachedValue();
        }

        return cachedValue;
    }

    public void setValue(E value) {
        this.setter.accept(value);
        this.updateCachedValue();
    }

    private void updateCachedValue() {
        this.cachedValue = this.getter.get();
    }

    private static String getNameTranslationKey(String configPath, String fieldName) {
        return "config." + configPath.replace('/', '.') + "." + fieldName;
    }

    private static String getDescriptionTranslationKey(String configPath, String fieldName) {
        return getNameTranslationKey(configPath, fieldName)+".description";
    }
}
