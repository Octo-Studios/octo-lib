package it.hurts.shatterbyte.shatterlib.client.config.widget;

import it.hurts.shatterbyte.shatterlib.client.config.AbstractEntryWidget;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Name;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericObjectWidget extends AbstractEntryWidget<Object> implements ContainerEventHandler {
    Map<FieldInfo, AbstractEntryWidget<?>> widgets = new LinkedHashMap<>();

    AbstractEntryWidget<?> focused;
    boolean dragging = false;

    @Getter
    public boolean collapsed = true;

    public GenericObjectWidget(ShatterConfig config, String fieldName, Object defaultValue, Supplier<Object> getter, Consumer<Object> setter) {
        super(config, fieldName, defaultValue, getter, setter, 0, 0, 100, 100);
        this.populateWidget();
    }

    @SneakyThrows
    private void populateWidget() {
        Object object = this.getValue();
        Class<?> clazz = object.getClass();

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(clazz, lookup);

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            if (field.isAnnotationPresent(Exclude.class)) {
                continue;
            }

            int mods = field.getModifiers();
            if (Modifier.isTransient(mods) || Modifier.isStatic(mods)) {
                continue;
            }

            AbstractEntryWidget<?> widget = AbstractEntryWidget.tryCreate(this.getPath(), this.config, privateLookup, field, object);

            if (widget == null) {
                continue;
            }

            String fieldName = field.getName();
            String fieldDescription = "";

            if (field.isAnnotationPresent(Name.class)) {
                fieldName = field.getAnnotation(Name.class).value();
            } else {
                fieldName = AbstractEntryWidget.convertFromCamelCase(fieldName);
            }

            if (field.isAnnotationPresent(Comment.class)) {
                fieldDescription = field.getAnnotation(Comment.class).value();
            }

            FieldInfo fieldInfo = new FieldInfo(fieldName, fieldDescription);

            widget.setParent(this);
            this.widgets.put(fieldInfo, widget);
        }
    }

    @Override
    protected void renderEntry(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.widgets.values().forEach(widget -> widget.render(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return new ArrayList<>(widgets.values());
    }

    @Override
    public boolean isDragging() {
        return this.dragging;
    }

    @Override
    public void setDragging(boolean isDragging) {
        this.dragging = isDragging;
    }

    @Override
    public @Nullable GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        this.focused = (AbstractEntryWidget<?>) focused;
    }

    record FieldInfo(String name, String description) {}
}
