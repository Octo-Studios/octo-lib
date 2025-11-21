package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Name;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractEntryWidget<E> extends AbstractWidget {
    @Getter
    private Component description;
    private E cachedValue;

    @Getter
    private final E defaultValue;

    private final Supplier<E> getter;
    private final Consumer<E> setter;

    public AbstractEntryWidget(E defaultValue, Supplier<E> getter, Consumer<E> setter, int x, int y, int width, int height, String configPath, String fieldName) {
        super(x, y, width, height, Component.literal(fieldName));
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

    public static String convertFromCamelCase(String varName) {
        String result = varName.replaceAll("([a-z])([A-Z])", "$1 $2");
        result = result.substring(0, 1).toUpperCase() + result.substring(1);
        return result;
    }
}
