package it.hurts.shatterbyte.shatterlib.client.config;

import it.hurts.shatterbyte.shatterlib.client.config.widget.FieldWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.ListWidget;
import it.hurts.shatterbyte.shatterlib.client.config.widget.SliderWidget;
import it.hurts.shatterbyte.shatterlib.client.screen.widget.Child;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Range;
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
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractEntryWidget<E> extends AbstractWidget implements Child<FieldWidget> {
    @Getter
    private final ShatterConfig config;
    private FieldWidget parent;
    private E cachedValue;

    @Getter
    private final E defaultValue;

    private final Supplier<E> getter;
    private final Consumer<E> setter;

    public AbstractEntryWidget(ShatterConfig config, FieldWidget parent, E defaultValue, Supplier<E> getter, Consumer<E> setter, int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        this.setParent(parent);

        this.config = config;
        this.defaultValue = defaultValue;
        this.getter = getter;
        this.setter = setter;

        this.updateCachedValue();
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> AbstractEntryWidget<T> tryCreate(String path, FieldWidget parent, ShatterConfig config, MethodHandles.Lookup privateLookup, Field field, Object object) {
        Class<?> type = field.getType();

        boolean hasRangeAndNumeric = field.isAnnotationPresent(Range.class) && isNumericType(type);
        EntryWidgetFactory<T> factory = EntryWidgetRegistry.getFactory(type);

        if (factory == null && !hasRangeAndNumeric) {
            return null;
        }

        // create getter and setter handles bound to the target object
        MethodHandle getterHandle = privateLookup.unreflectGetter(field).bindTo(object);
        MethodHandle setterHandle = privateLookup.unreflectSetter(field).bindTo(object);

        String fieldName = field.getName();

        String newPath = path + "." + fieldName;
        if (newPath.startsWith(".")) {
            newPath = newPath.substring(1);
        }

        Optional<T> defaultValue = config.getDefaultValue(newPath, field.getGenericType());

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

        if (List.class.isAssignableFrom(type)) {
            if (!(field.getGenericType() instanceof ParameterizedType pt)) {
                throw new RuntimeException("List field without generic type: " + field);
            }

            Type arg = pt.getActualTypeArguments()[0];

            Class<?> rawElementClass;
            if (arg instanceof Class<?> c) {
                rawElementClass = c;
            } else if (arg instanceof ParameterizedType p) {
                rawElementClass = (Class<?>) p.getRawType();
            } else {
                throw new RuntimeException("Unsupported list element type: " + arg);
            }

            @SuppressWarnings("unchecked")
            Class<Object> elementClass = (Class<Object>) rawElementClass;

            @SuppressWarnings("unchecked")
            ArrayList<Object> dv = (ArrayList<Object>) defaultValue.get();

            Supplier<ArrayList<Object>> listGetter =
                    () -> (ArrayList<Object>) getter.get();

            Consumer<ArrayList<Object>> listSetter =
                    v -> setter.accept((T) v);

            return (AbstractEntryWidget<T>) new ListWidget<>(
                    config,
                    parent,
                    dv,
                    listGetter,
                    listSetter,
                    elementClass
            );
        }


        if (defaultValue.isEmpty()) {
            throw new RuntimeException("Default value for " + newPath + " not found.");
        }

        if (hasRangeAndNumeric) {
            // ensure runtime value is a Number
            Object dv = defaultValue.get();
            if (!(dv instanceof Number defaultNumber)) {
                throw new RuntimeException("Default value for " + newPath + " is not a Number but field is numeric.");
            }

            Supplier<Number> numGetter = () -> (Number) getter.get();
            Consumer<Number> numSetter = (num) -> setter.accept((T) num);

            SliderWidget<Number> slider = new SliderWidget<>(config, parent, defaultNumber, numGetter, numSetter);

            Range rangeAnn = field.getAnnotation(Range.class);
            slider.setRange(rangeAnn.min(), rangeAnn.max(), rangeAnn.step());

            // safe-ish unchecked cast to match return type
            return (AbstractEntryWidget<T>) slider;
        }

        AbstractEntryWidget<T> widget = factory.create(
                config,
                parent,
                defaultValue.get(),
                getter,
                setter
        );

        return widget;
    }

    private static boolean isNumericType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return clazz == byte.class || clazz == short.class || clazz == int.class || clazz == long.class
                    || clazz == float.class || clazz == double.class;
        } else {
            return Number.class.isAssignableFrom(clazz);
        }
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
    public @Nullable FieldWidget getParent() {
        return parent;
    }

    @Override
    public void setParent(FieldWidget parent) {
        this.parent = parent;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
