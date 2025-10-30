package it.hurts.shatterbyte.shatterlib.module.config;

import de.marhali.json5.*;
import it.hurts.shatterbyte.shatterlib.module.config.type.AbstractEntry;
import it.hurts.shatterbyte.shatterlib.module.config.type.adapter.ShatterColorAdapter;
import it.hurts.shatterbyte.shatterlib.module.config.type.adapter.TypeAdapter;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.util.*;

public class Json5Utils {
    private static final Unsafe unsafe;
    private static final Map<Type, TypeAdapter<?>> ADAPTERS = new HashMap<>();

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get sun.misc.Unsafe", e);
        }

        registerAdapter(ShatterColor.class, new ShatterColorAdapter());
    }

    public static <T> void registerAdapter(Type type, TypeAdapter<T> adapter) {
        ADAPTERS.put(type, adapter);
    }

    public static <T> Json5Element encode(T value) {
        if (value == null) {
            return new Json5Null();
        }

        @SuppressWarnings("unchecked")
        TypeAdapter<T> customAdapter = (TypeAdapter<T>) ADAPTERS.get(value.getClass());
        if (customAdapter != null) {
            return customAdapter.encode(value);
        }

        return switch (value) {
            case String string -> Json5Primitive.fromString(string);
            case Number number -> Json5Primitive.fromNumber(number);
            case Boolean bool -> Json5Primitive.fromBoolean(bool);
            case Collection<?> collection -> {
                Json5Array array = new Json5Array();
                for (Object item : collection) {
                    array.add(Json5Utils.encode(item));
                }
                yield array;
            }
            case Map<?, ?> map -> {
                Json5Object obj = new Json5Object();
                map.forEach((key1, value1) -> {
                    String key = String.valueOf(key1);
                    obj.add(key, Json5Utils.encode(value1));
                });
                yield obj;
            }
            default -> {
                try {
                    Json5Object obj = new Json5Object();
                    Class<?> clazz = value.getClass();

                    for (Field field : clazz.getDeclaredFields()) {
                        if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                            continue;
                        }

                        field.setAccessible(true);

                        String fieldName = field.getName();
                        Object fieldValue = field.get(value);

                        if (fieldValue instanceof AbstractEntry<?, ?> entry) {
                            obj.add(fieldName, entry.saveToJson());
                            continue;
                        }

                        obj.add(fieldName, encode(fieldValue));
                    }

                    yield obj;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Failed to encode object via reflection", e);
                }
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static <T> T decode(Json5Element json, Type type) {
        if (json == null || json.isJson5Null()) {
            return null;
        }

        TypeAdapter<T> customAdapter = (TypeAdapter<T>) ADAPTERS.get(type);
        if (customAdapter != null) {
            return customAdapter.decode(json);
        }

        Class<T> rawClass = (Class<T>) getRawClass(type);

        if (json.isJson5Primitive()) {
            if (rawClass == String.class) {
                return (T) json.getAsString();
            }
            if ((rawClass == Boolean.class || rawClass == boolean.class)) {
                return (T) Boolean.valueOf(json.getAsBoolean());
            }
            if ((rawClass == Integer.class || rawClass == int.class)) {
                return (T) Integer.valueOf(json.getAsInt());
            }
            if ((rawClass == Long.class || rawClass == long.class)) {
                return (T) Long.valueOf(json.getAsLong());
            }
            if ((rawClass == Double.class || rawClass == double.class)) {
                return (T) Double.valueOf(json.getAsDouble());
            }
        }

        if (Collection.class.isAssignableFrom(rawClass) && json.isJson5Array()) {
            Collection<Object> collection;
            if (Set.class.isAssignableFrom(rawClass)) {
                collection = new HashSet<>();
            } else {
                collection = new ArrayList<>();
            }

            Type itemType;
            if (type instanceof ParameterizedType) {
                itemType = ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                throw new IllegalArgumentException("Cannot decode raw Collection. Use generics (e.g., List<String>).");
            }

            for (Json5Element itemJson : json.getAsJson5Array()) {
                Object item = decode(itemJson, itemType);
                collection.add(item);
            }

            return (T) collection;
        }

        if (Map.class.isAssignableFrom(rawClass) && json.isJson5Object()) {
            Map<String, Object> map = new LinkedHashMap<>();

            Type valueType = Object.class;
            if (type instanceof ParameterizedType) {
                Type[] typeArgs = ((ParameterizedType) type).getActualTypeArguments();
                if (typeArgs.length == 2) {
                    if (!getRawClass(typeArgs[0]).equals(String.class)) {
                        throw new IllegalArgumentException("Default decoder only supports Map with String keys.");
                    }
                    valueType = typeArgs[1]; // Get the value type
                }
            } else {
                throw new IllegalArgumentException("Cannot decode raw Map. Use generics (e.g., Map<String, MyObject>).");
            }

            for (Map.Entry<String, Json5Element> entry : json.getAsJson5Object().entrySet()) {
                String key = entry.getKey();
                Object value = decode(entry.getValue(), valueType);
                map.put(key, value);
            }
            return (T) map;
        }

        if (json.isJson5Object()) {
            try {
                Constructor<T> constructor = rawClass.getDeclaredConstructor();
                constructor.setAccessible(true);

                T newInstance = constructor.newInstance();

                Json5Object obj = json.getAsJson5Object();
                for (Map.Entry<String, Json5Element> entry : obj.entrySet()) {
                    String fieldName = entry.getKey();
                    Json5Element fieldValueJson = entry.getValue();

                    try {
                        Field field = rawClass.getDeclaredField(fieldName);
                        field.setAccessible(true);

                        Type fieldType = field.getGenericType();
                        Object decodedValue = decode(fieldValueJson, fieldType);

                        field.set(newInstance, decodedValue);

                    } catch (NoSuchFieldException e) {
                        // Field exists in JSON but not in Java class.
                        // Default behavior: ignore it.
                    }
                }
                return newInstance;

            } catch (Exception e) {
                throw new RuntimeException("Failed to decode object via reflection for type: " + type.getTypeName(), e);
            }
        }

        throw new IllegalArgumentException("Don't know how to decode " + json.getClass().getSimpleName() + " into type " + type.getTypeName());
    }

    private static Class<?> getRawClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            return Array.newInstance(getRawClass(componentType), 0).getClass();
        }
        if (type instanceof WildcardType) {
            return getRawClass(((WildcardType) type).getUpperBounds()[0]);
        }
        throw new IllegalArgumentException("Cannot determine raw class for Type: " + type.getTypeName());
    }
}
