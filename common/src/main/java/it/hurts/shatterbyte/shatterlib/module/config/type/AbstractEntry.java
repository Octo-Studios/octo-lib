package it.hurts.shatterbyte.shatterlib.module.config.type;

import com.google.gson.Gson;
import de.marhali.json5.*;
import it.hurts.shatterbyte.shatterlib.module.config.Json5Deserializer;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
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
            Object deserialized = Json5Deserializer.deserializeObject(element, this.getValue().getClass());
            this.setValue((T) deserialized);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load entry from json", e);
        }
    }

    public Json5Element saveToJson() {
        try {
            return serializeObject(this.getValue());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to save entry to json", e);
        }
    }

    public static Json5Element serializeObject(Object object) throws IllegalAccessException {
        return switch (object) {
            case null -> new Json5Null();
            case Number number -> Json5Primitive.fromNumber(number);
            case Boolean b -> Json5Primitive.fromBoolean(b);
            case String s -> Json5Primitive.fromString(s);
            case Character c -> Json5Primitive.fromCharacter(c);
            case Instant instant -> Json5Primitive.fromInstant(instant);
            case AbstractEntry<?, ?> entry -> {
                Json5Element element = entry.saveToJson();
                element.setComment(entry.getComment());
                yield element;
            }
            default -> {
                Class<?> clazz = object.getClass();

                // enums -> name()
                if (clazz.isEnum()) {
                    yield Json5Primitive.fromString(((Enum<?>) object).name());
                }

                // arrays
                if (clazz.isArray()) {
                    Json5Array arr = new Json5Array();
                    int len = Array.getLength(object);
                    for (int i = 0; i < len; i++) {
                        arr.add(serializeObject(Array.get(object, i)));
                    }
                    yield arr;
                }

                // collections
                if (object instanceof Collection<?>) {
                    Json5Array arr = new Json5Array();
                    for (Object item : (Collection<?>) object) {
                        arr.add(serializeObject(item));
                    }
                    yield arr;
                }

                // maps
                if (object instanceof Map<?, ?>) {
                    Json5Object json = new Json5Object();
                    for (Map.Entry<?, ?> e : ((Map<?, ?>) object).entrySet()) {
                        String key = (e.getKey() == null) ? "null" : e.getKey().toString();
                        json.add(key, serializeObject(e.getValue()));
                    }
                    yield json;
                }

                // treat JDK/core classes as leaves to avoid reflecting into them (avoids IllegalAccess)
                Package pkg = clazz.getPackage();
                String pkgName = (pkg == null) ? "" : pkg.getName();
                if (pkgName.startsWith("java.") || pkgName.startsWith("javax.") || pkgName.startsWith("kotlin.")) {
                    // fallback: use toString() as a simple representation
                    yield Json5Primitive.fromString(object.toString());
                }

                // ---- otherwise reflect into user-defined class fields ----
                Json5Object json5Object = new Json5Object();
                for (Field field : clazz.getDeclaredFields()) {
                    // skip static and transient fields (like gson does)
                    int mods = field.getModifiers();
                    if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) continue;

                    field.setAccessible(true);
                    Object fieldValue = field.get(object);
                    String name = field.getName();

                    if (fieldValue == null) {
                        json5Object.add(name, new Json5Null());
                    } else {
                        json5Object.add(name, serializeObject(fieldValue));
                    }
                }
                yield json5Object;
            }
        };

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
