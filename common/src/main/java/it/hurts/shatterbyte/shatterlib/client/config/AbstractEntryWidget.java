package it.hurts.shatterbyte.shatterlib.client.config;

import lombok.Getter;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractEntryWidget<E> extends AbstractWidget {
    private final Supplier<E> getter;
    private final Consumer<E> setter;

    public AbstractEntryWidget(Supplier<E> getter, Consumer<E> setter, int x, int y, int width, int height, Component name) {
        super(x, y, width, height, name);
        this.getter = getter;
        this.setter = setter;
    }

    public E getValue() {
        return this.getter.get();
    }

    public void setValue(E value) {
        this.setter.accept(value);
    }
}
