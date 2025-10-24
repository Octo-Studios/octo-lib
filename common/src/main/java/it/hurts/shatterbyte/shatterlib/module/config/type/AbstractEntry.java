package it.hurts.shatterbyte.shatterlib.module.config.type;

import de.marhali.json5.*;
import it.hurts.shatterbyte.shatterlib.module.config.Json5Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
public abstract class AbstractEntry<T, E extends AbstractEntry<T, E>> {
    private String comment = "";

    @Setter
    private T value;
    final T defaultValue;

    @SuppressWarnings("unchecked")
    AbstractEntry(T defaultValue) {
        this.value = defaultValue;

        try {
            Json5Element json = Json5Utils.serializeObject(value);
            this.defaultValue = (T) Json5Utils.deserializeObject(json, value.getClass());
        } catch (Exception e) {
            throw new RuntimeException("Couldn't deep copy object: "+value.getClass().getName(), e);
        }
    }

    void setComment(String comment) {
        this.comment = comment;
    }

    @SuppressWarnings("unchecked")
    public void loadFromJson(Json5Element element) {
        try {
            Object deserialized = Json5Utils.deserializeObject(element, this.getValue().getClass());
            this.setValue((T) deserialized);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load entry from json", e);
        }
    }

    public final Json5Element saveToJson() {
        return saveToJson(this.getValue());
    }

    public Json5Element saveToJson(T value) {
        try {
            return Json5Utils.serializeObject(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to save entry to json", e);
        }
    }

    public static abstract class Builder<T, E extends AbstractEntry<T, E>, B extends Builder<T, E, B>> {
        protected T value;
        protected String comment = "";

        protected Builder(T defaultValue) {
            this.value = defaultValue;
        }

        @SuppressWarnings("unchecked")
        public B withComment(String comment) {
            this.comment = comment;
            return (B) this;
        }

        protected abstract E createEntry(T defaultValue);

        public E build() {
            E entry = createEntry(value);
            entry.setComment(comment);
            return entry;
        }
    }
}
