package it.hurts.shatterbyte.shatterlib.module.config;

import de.marhali.json5.*;
import de.marhali.json5.config.Json5Options;
import it.hurts.shatterbyte.shatterlib.module.config.type.adapter.ResourceLocationAdapter;
import it.hurts.shatterbyte.shatterlib.module.config.type.adapter.ShatterColorAdapter;
import it.hurts.shatterbyte.shatterlib.module.config.type.adapter.TypeAdapter;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Comment;
import it.hurts.shatterbyte.shatterlib.module.config.type.annotation.Exclude;
import it.hurts.shatterbyte.shatterlib.util.ShatterColor;
import net.minecraft.resources.ResourceLocation;

import java.lang.reflect.*;
import java.util.*;

public class Json5Utils {
    private static final Map<Type, TypeAdapter<?>> ADAPTERS = new HashMap<>();
    public static final Json5Options PRETTY_PRINT = Json5Options.builder().prettyPrinting().quoteless().build();

    static {
        registerAdapter(ShatterColor.class, new ShatterColorAdapter());
        registerAdapter(ResourceLocation.class, new ResourceLocationAdapter());
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

    public static void injectDefaultComments(Json5Object root, Json5Object defaultSchema) {
        if (defaultSchema == null) {
            return;
        }

        for (Map.Entry<String, Json5Element> entry : root.entrySet()) {
            String key = entry.getKey();
            Json5Element child = entry.getValue();
            Json5Element defChild = defaultSchema.has(key) ? defaultSchema.get(key) : null;
            injectDefaultCommentsRecursive(child, defChild);
        }
    }

    private static void injectDefaultCommentsRecursive(Json5Element target, Json5Element def) {
        if (target == null || def == null) {
            return;
        }

        if (Json5Utils.appendDefaultComment(target, def)) {
            return;
        }

        // object -> recurse by key
        if (target.isJson5Object() && def.isJson5Object()) {
            Json5Object targetObj = target.getAsJson5Object();
            Json5Object defObj = def.getAsJson5Object();

            for (Map.Entry<String, Json5Element> entry : targetObj.entrySet()) {
                String key = entry.getKey();
                Json5Element child = entry.getValue();
                Json5Element defChild = defObj.has(key) ? defObj.get(key) : null;
                injectDefaultCommentsRecursive(child, defChild);
            }

            return;
        }

        // array -> recurse by index
        if (target.isJson5Array() && def.isJson5Array()) {
            Json5Array targetArr = target.getAsJson5Array();
            Json5Array defArr = def.getAsJson5Array();
            int size = Math.min(targetArr.size(), defArr.size());
            for (int i = 0; i < size; i++) {
                injectDefaultCommentsRecursive(targetArr.get(i), defArr.get(i));
            }
        }
    }

    private static boolean appendDefaultComment(Json5Element target, Json5Element def) {
        String defaultText = def.toString(PRETTY_PRINT);

        if (defaultText.lines().count() > 10) {
            return false;
        }

        String existing = target.getComment();
        StringBuilder newComment = new StringBuilder();
        if (existing != null && !existing.isEmpty()) {
            newComment.append(existing.trim());
            newComment.append("\n\n");
        }
        newComment.append("Default: ").append(defaultText);

        target.setComment(newComment.toString());
        return true;
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
