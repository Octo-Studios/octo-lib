package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.client.config.widget.GenericObjectWidget;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Name;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractEntryWidget<E> extends AbstractWidget implements Child<GenericObjectWidget> {
    protected final ShatterConfig config;

    private GenericObjectWidget parent;
    private E cachedValue;

    @Getter
    private final E defaultValue;

    private final Supplier<E> getter;
    private final Consumer<E> setter;

    public AbstractEntryWidget(ShatterConfig config, E defaultValue, Supplier<E> getter, Consumer<E> setter, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.config = config;
        this.defaultValue = defaultValue;
        this.getter = getter;
        this.setter = setter;

        this.updateCachedValue();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    protected static <T> AbstractEntryWidget<T> tryCreate(String path, ShatterConfig config, MethodHandles.Lookup privateLookup, Field field, Object object) {
        EntryWidgetFactory<T> factory = EntryWidgetRegistry.getFactory(field.getType());
        if (factory == null) {
            return null;
        }

        MethodHandle getterHandle = privateLookup.unreflectGetter(field).bindTo(object);
        MethodHandle setterHandle = privateLookup.unreflectSetter(field).bindTo(object);

        Supplier<Object> getter = getterHandle::invoke;
        Consumer<T> setter = setterHandle::invoke;

        String fieldName = field.getName();

        String newPath = path + "." + fieldName;
        if (newPath.startsWith(".")) {
            newPath = newPath.substring(1);
        }

        Optional<T> defaultValue = config.getDefaultValue(newPath, field.getGenericType());

        if (defaultValue.isEmpty()) {
            throw new RuntimeException("Default value for " + newPath + " not found.");
        }

        return factory.create(
                config,
                defaultValue.get(),
                (Supplier<T>) getter,
                setter
        );
    }

    @Override
    protected final void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderEntry(guiGraphics, mouseX, mouseY, partialTick);
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

    public void resetValue() {
        this.setValue(this.getDefaultValue());
    }

    @Override
    public @Nullable GenericObjectWidget getParent() {
        return parent;
    }

    @Override
    public void setParent(GenericObjectWidget parent) {
        this.parent = parent;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
