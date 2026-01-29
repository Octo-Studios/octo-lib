package it.hurts.shatterbyte.shatterlib.module.config.util;

import de.marhali.json5.*;
import de.marhali.json5.config.Json5Options;
import it.hurts.shatterbyte.shatterlib.module.config.ShatterConfig;
import it.hurts.shatterbyte.shatterlib.module.config.type.adapter.IdentifierAdapter;
import it.hurts.shatterbyte.shatterlib.module.config.type.adapter.ShatterColorAdapter;
import it.hurts.shatterbyte.shatterlib.module.config.type.adapter.TypeAdapter;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import net.minecraft.resources.Identifier;

import java.lang.reflect.*;
import java.util.*;

import static it.hurts.shatterbyte.shatterlib.ShatterLib.LOGGER;

public class Json5Utils {
    private static final Map<Type, TypeAdapter<?>> ADAPTERS = new HashMap<>();
    public static final Json5Options PRETTY_PRINT = Json5Options.builder().prettyPrinting().quoteless().build();

    static {
        registerAdapter(ShatterColor.class, new ShatterColorAdapter());
        registerAdapter(Identifier.class, new IdentifierAdapter());
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
            case Enum<?> enumValue -> Json5Primitive.fromString(enumValue.name());
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

                    if (value instanceof ShatterConfig config) {
                        Json5Element schemaVersion = Json5Primitive.fromNumber(config.getSchemaVersion());
                        schemaVersion.setComment("DO NOT CHANGE");
                        obj.add("schemaVersion", schemaVersion);
                    }

                    for (Field field : getAllFields(clazz)) {
                        if (Modifier.isTransient(field.getModifiers()) || Modifier.isStatic(field.getModifiers())) {
                            continue;
                        }
                        if (field.isAnnotationPresent(Exclude.class)) {
                            continue;
                        }

                        field.setAccessible(true);

                        String fieldName = field.getName();
                        Object fieldValue = field.get(value);

                        Json5Element fieldElement = Json5Utils.encode(fieldValue);

                        if (field.isAnnotationPresent(Comment.class)) {
                            Comment comment = field.getAnnotation(Comment.class);
                            if (!comment.value().isEmpty()) {
                                fieldElement.setComment(comment.value());
                            }
                        }

                        if (fieldValue.getClass().isEnum()) {
                            Json5Utils.appendEnumComments(fieldValue, fieldElement);
                        }

                        obj.add(fieldName, fieldElement);
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
            if ((rawClass == Float.class || rawClass == float.class)) {
                return (T) Float.valueOf(json.getAsFloat());
            }
            if (rawClass.isEnum()) {
                return (T) Enum.valueOf((Class<Enum>) rawClass, json.getAsString());
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
                        Field field = findFieldInHierarchy(rawClass, fieldName);
                        field.setAccessible(true);
                        if (field.isAnnotationPresent(Exclude.class)) {
                            continue;
                        }

                        Type fieldType = field.getGenericType();
                        Object decodedValue = decode(fieldValueJson, fieldType);

                        field.set(newInstance, decodedValue);

                    } catch (NoSuchFieldException e) {
                        // Field exists in JSON but not in Java class.
                        // Default behavior: ignore it.
                    }
                }
                return newInstance;

            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Couldn't find an empty constructor for type: " + type.getTypeName(), e);
            } catch (Exception e) {
                throw new RuntimeException("Failed to decode object via reflection for type: " + type.getTypeName(), e);
            }
        }

        throw new IllegalArgumentException("Don't know how to decode " + json.getClass().getSimpleName() + " into type " + type.getTypeName());
    }

    public static <T> void deserializeObject(Json5Object json5Object, T object) {
        for (Field field : object.getClass().getDeclaredFields()) {
            int mods = field.getModifiers();
            if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) {
                continue;
            }

            if (field.isAnnotationPresent(Exclude.class)) {
                continue;
            }

            String fieldName = field.getName();

            if (!json5Object.has(fieldName)) {
                continue;
            }

            try {
                field.setAccessible(true);
                Json5Element fieldElement = json5Object.get(fieldName);
                Type type = field.getGenericType();
                field.set(object, Json5Utils.decode(fieldElement, type));
            } catch (Exception e) {
                LOGGER.warn("Failed to load field '{}', using default.", fieldName, e);
            }
        }
    }

    private static <T> void appendEnumComments(T value, Json5Element element) {
        String comment = element.hasComment() ? element.getComment() : "";
        if (!comment.isEmpty()) {
            comment += "\n\n";
        }

        Object[] enumConstants = value.getClass().getEnumConstants();

        if (enumConstants != null && enumConstants.length > 0) {
            comment += "Values: ";

            for (int i = 0; i < enumConstants.length; i++) {
                comment += enumConstants[i].toString();
                if (i < enumConstants.length - 1) {
                    comment += ", ";
                }
            }
        }

        element.setComment(comment);
    }

    // helper: return all declared fields up the class hierarchy (excluding Object.class)
    private static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            Field[] declared = current.getDeclaredFields();
            fields.addAll(Arrays.asList(declared));
            current = current.getSuperclass();
        }
        return fields;
    }

    // helper: find a field by name walking up the class hierarchy
    private static Field findFieldInHierarchy(Class<?> clazz, String name) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(name);
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
