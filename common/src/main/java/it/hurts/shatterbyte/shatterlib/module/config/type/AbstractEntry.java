package it.hurts.shatterbyte.shatterlib.module.config.type;

import de.marhali.json5.*;
import it.hurts.shatterbyte.shatterlib.module.config.Json5Utils;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.*;

@Getter
public abstract class AbstractEntry<T, E extends AbstractEntry<T, E>> {
    private String comment = "";

    @Setter
    private T value;

    AbstractEntry(T defaultValue) {
        this.value = defaultValue;
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

    public Json5Element saveToJson() {
        try {
            return Json5Utils.serializeObject(this.getValue());
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
