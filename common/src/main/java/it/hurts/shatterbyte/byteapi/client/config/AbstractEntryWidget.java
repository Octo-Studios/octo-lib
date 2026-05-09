package it.hurts.shatterbyte.byteapi.client.config;

import it.hurts.shatterbyte.byteapi.client.config.widget.FieldWidget;
import it.hurts.shatterbyte.byteapi.client.config.widget.PathContainerWidget;
import it.hurts.shatterbyte.byteapi.client.screen.widget.Child;
import it.hurts.shatterbyte.byteapi.module.config.ShatterConfig;
import it.hurts.shatterbyte.byteapi.module.config.util.Json5Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractEntryWidget<E> extends AbstractWidget implements Child<PathContainerWidget> {
    @Getter
    private final ShatterConfig config;
    private PathContainerWidget parent;
    private E cachedValue;

    @Getter
    private final E defaultValue;
    private boolean defaultStateDirty = true;
    private boolean defaultState = true;

    private final Supplier<E> getter;
    private final Consumer<E> setter;

    public AbstractEntryWidget(ShatterConfig config, PathContainerWidget parent, E defaultValue, Supplier<E> getter, Consumer<E> setter, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.setParent(parent);

        this.config = config;
        this.defaultValue = defaultValue;
        this.getter = getter;
        this.setter = setter;

        this.updateCachedValue();
    }

    protected void requestRelayout() {
        PathContainerWidget parent = this.parent;
        if (parent != null) {
            parent.requestRelayout();
        }
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> AbstractEntryWidget<?> tryCreate(String path, FieldWidget parent, ShatterConfig config, MethodHandles.Lookup privateLookup, Field field, Object object, Object defaultObject) {
        Class<?> type = field.getType();

        EntryWidgetFactory<T> factory = EntryWidgetRegistry.getFactory(type);

        if (factory == null) {
            return null;
        }

        MethodHandle getterHandle = privateLookup.unreflectGetter(field).bindTo(object);
        MethodHandle setterHandle = privateLookup.unreflectSetter(field).bindTo(object);

        String fieldName = field.getName();

        String newPath = path + "." + fieldName;
        if (newPath.startsWith(".")) {
            newPath = newPath.substring(1);
        }

        Optional<T> defaultValue = config.getDefaultValue(newPath, field.getGenericType());

        if (defaultValue.isEmpty() && defaultObject != null) {
            Field defaultField = findFieldInHierarchy(defaultObject.getClass(), fieldName);
            defaultField.setAccessible(true);
            defaultValue = (Optional<T>) Optional.ofNullable(defaultField.get(defaultObject));
        }

        Supplier<T> getter = () -> {
            try {
                return (T) getterHandle.invoke();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };

        Consumer<T> setter = (v) -> {
            try {
                setterHandle.invoke(v);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };

        if (defaultValue.isEmpty()) {
            throw new RuntimeException("Default value for " + newPath + " not found.");
        }

        return factory.create(
                config,
                field.getGenericType(),
                field.getDeclaredAnnotations(),
                parent,
                defaultValue.get(),
                getter,
                setter
        );
    }

    private static boolean isNumericType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return clazz == byte.class || clazz == short.class || clazz == int.class || clazz == long.class
                    || clazz == float.class || clazz == double.class;
        } else {
            return Number.class.isAssignableFrom(clazz);
        }
    }

    private static Field findFieldInHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }

        throw new NoSuchFieldException(fieldName);
    }

    @Override
    protected final void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderEntry(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected abstract void renderEntry(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick);

    public E getValue() {
        if (this.cachedValue == null) {
            this.updateCachedValue();
        }

        return cachedValue;
    }

    public void setValue(E value) {
        this.setter.accept(value);
        this.updateCachedValue();
        this.defaultStateDirty = true;
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

    public boolean isAtDefaultValue() {
        E currentValue = this.getValue();
        E defaultValue = this.getDefaultValue();

        if (currentValue == defaultValue) {
            return true;
        }

        if (needsStructuralComparison(currentValue, defaultValue)) {
            try {
                return Objects.equals(Json5Utils.encode(currentValue), Json5Utils.encode(defaultValue));
            } catch (Throwable ignored) {}
        }

        if (defaultStateDirty) {
            defaultState = Objects.equals(currentValue, defaultValue);
            defaultStateDirty = false;
        }

        return defaultState;
    }

    private static boolean needsStructuralComparison(@Nullable Object currentValue, @Nullable Object defaultValue) {
        if (currentValue == null || defaultValue == null) {
            return false;
        }

        return !isSimpleComparable(currentValue) || !isSimpleComparable(defaultValue);
    }

    private static boolean isSimpleComparable(Object value) {
        return value instanceof Number
                || value instanceof CharSequence
                || value instanceof Boolean
                || value instanceof Character
                || value instanceof Enum<?>;
    }

    @Override
    public @Nullable PathContainerWidget getParent() {
        return parent;
    }

    @Override
    public void setParent(PathContainerWidget parent) {
        this.parent = parent;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
