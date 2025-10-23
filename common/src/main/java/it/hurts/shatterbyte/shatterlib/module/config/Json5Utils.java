package it.hurts.shatterbyte.shatterlib.module.config;

import de.marhali.json5.*;
import it.hurts.shatterbyte.shatterlib.module.config.type.AbstractEntry;
import sun.misc.Unsafe;

import java.lang.reflect.*;
import java.time.Instant;
import java.util.*;

public class Json5Utils {
    private static final Unsafe unsafe;
    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get sun.misc.Unsafe", e);
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

    @SuppressWarnings("unchecked")
    public static <T> T deserializeObject(Json5Element element, Class<T> clazz) throws Exception {
        return (T) deserializeObject(element, (Type) clazz);
    }

    public static Object deserializeObject(Json5Element element, Type type) throws Exception {
        if (element == null || element.isJson5Null()) {
            return null;
        }

        Class<?> rawClass = getRawClass(type);

        // 1. Handle untyped (Object.class)
        if (rawClass == Object.class) {
            return deserializeUntyped(element);
        }

        // 2. Handle Primitives
        if (element.isJson5Primitive()) {
            Json5Primitive primitive = element.getAsJson5Primitive();
            return deserializePrimitive(primitive, rawClass);
        }

        // 3. Handle Arrays and Collections
        if (element.isJson5Array()) {
            Json5Array jsonArray = element.getAsJson5Array();
            return deserializeArray(jsonArray, type, rawClass);
        }

        // 4. Handle Objects and Maps
        if (element.isJson5Object()) {
            Json5Object jsonObject = element.getAsJson5Object();
            return deserializeObject(jsonObject, type, rawClass);
        }

        throw new IllegalArgumentException("Unhandled JSON type for deserialization: " + element.getClass().getName());
    }

    /**
     * Deserializes a Json5Primitive to a specific class.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object deserializePrimitive(Json5Primitive primitive, Class<?> clazz) throws Exception {
        if (clazz == String.class) {
            return primitive.getAsString();
        }
        if (clazz == Character.class || clazz == char.class) {
            String s = primitive.getAsString();
            if (s != null && s.length() == 1) {
                return s.charAt(0);
            }
            throw new IllegalArgumentException("Cannot deserialize string \"" + s + "\" to Character.");
        }
        if (clazz == Boolean.class || clazz == boolean.class) {
            return primitive.getAsBoolean();
        }

        if (clazz == Instant.class) {
            return primitive.getAsInstant();
        }

        if (Number.class.isAssignableFrom(clazz) || clazz.isPrimitive()) {
            Number n = primitive.getAsNumber();
            if (clazz == Integer.class || clazz == int.class) return n.intValue();
            if (clazz == Long.class || clazz == long.class) return n.longValue();
            if (clazz == Double.class || clazz == double.class) return n.doubleValue();
            if (clazz == Float.class || clazz == float.class) return n.floatValue();
            if (clazz == Short.class || clazz == short.class) return n.shortValue();
            if (clazz == Byte.class || clazz == byte.class) return n.byteValue();
            return n;
        }

        if (clazz.isEnum()) {
            try {
                return Enum.valueOf((Class<Enum>) clazz, primitive.getAsString());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid enum constant " + primitive.getAsString() + " for " + clazz.getName());
            }
        }

        // Handle known JDK classes that were serialized with toString()
        Package pkg = clazz.getPackage();
        String pkgName = (pkg == null) ? "" : pkg.getName();
        if (pkgName.startsWith("java.") || pkgName.startsWith("javax.") || pkgName.startsWith("kotlin.")) {
            try {
                // Try a constructor that takes a String
                Constructor<?> ctor = clazz.getConstructor(String.class);
                ctor.setAccessible(true);
                return ctor.newInstance(primitive.getAsString());
            } catch (Exception e) {
                throw new InstantiationException();
            }
        }

        throw new IllegalArgumentException("Cannot deserialize primitive " + primitive.getAsString() + " to " + clazz.getName());
    }

    @SuppressWarnings({"unchecked"})
    private static Object deserializeArray(Json5Array jsonArray, Type type, Class<?> rawClass) throws Exception {
        if (rawClass.isArray()) {
            Type componentType = rawClass.getComponentType();
            Object newArray = Array.newInstance(getRawClass(componentType), jsonArray.size());
            for (int i = 0; i < jsonArray.size(); i++) {
                Object item = deserializeObject(jsonArray.get(i), componentType);
                Array.set(newArray, i, item);
            }
            return newArray;
        }

        if (Collection.class.isAssignableFrom(rawClass)) {
            // Determine the generic type of the collection's items
            Type itemType = Object.class;
            if (type instanceof ParameterizedType) {
                itemType = ((ParameterizedType) type).getActualTypeArguments()[0];
            }

            // Create an appropriate Collection instance
            Collection<Object> collection;
            if (rawClass.isInterface() || Modifier.isAbstract(rawClass.getModifiers())) {
                if (Set.class.isAssignableFrom(rawClass)) {
                    collection = new HashSet<>();
                } else {
                    collection = new ArrayList<>();
                }
            } else {
                collection = (Collection<Object>) rawClass.getDeclaredConstructor().newInstance();
            }

            // Recursively deserialize each item
            for (Json5Element itemElement : jsonArray) {
                collection.add(deserializeObject(itemElement, itemType));
            }
            return collection;
        }

        throw new IllegalArgumentException("Cannot deserialize Json5Array to " + type.getTypeName());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Object deserializeObject(Json5Object jsonObject, Type type, Class<?> rawClass) throws Exception {
        if (Map.class.isAssignableFrom(rawClass)) {
            // Determine the generic type of the map's values
            Type valueType = Object.class;
            if (type instanceof ParameterizedType) {
                valueType = ((ParameterizedType) type).getActualTypeArguments()[1];
            }

            // Create an appropriate Map instance
            Map<String, Object> map;
            if (rawClass.isInterface() || Modifier.isAbstract(rawClass.getModifiers())) {
                map = new LinkedHashMap<>(); // Preserve order, good default
            } else {
                map = (Map<String, Object>) rawClass.getDeclaredConstructor().newInstance();
            }

            // Recursively deserialize each value
            for (Map.Entry<String, Json5Element> entry : jsonObject.entrySet()) {
                Object value = deserializeObject(entry.getValue(), valueType);
                map.put(entry.getKey(), value);
            }
            return map;
        }

        if (AbstractEntry.class.isAssignableFrom(rawClass)) {
            try {
                Constructor<?> ctor = rawClass.getDeclaredConstructor();
                ctor.setAccessible(true);
                AbstractEntry instance = (AbstractEntry) ctor.newInstance();
                
                instance.loadFromJson(jsonObject);
                return instance;
            } catch (Exception e) {
                throw new InstantiationException();
            }
        }

        Object instance;
        try {
            Constructor<?> ctor = rawClass.getDeclaredConstructor();
            ctor.setAccessible(true);
            instance = ctor.newInstance();
        } catch (NoSuchMethodException e) {
            // Fallback to Unsafe
            try {
                instance = unsafe.allocateInstance(rawClass);
            } catch (InstantiationException unsafeError) {
                throw new InstantiationException();
            }
        } catch (Exception e) {
            // Other ctor.newInstance() errors
            throw new InstantiationException();
        }

        // Use reflection to set fields
        for (Map.Entry<String, Json5Element> entry : jsonObject.entrySet()) {
            String fieldName = entry.getKey();
            Json5Element fieldValueElement = entry.getValue();

            try {
                Field field = getField(rawClass, fieldName);
                
                int mods = field.getModifiers();
                if (Modifier.isStatic(mods) || Modifier.isTransient(mods)) {
                    continue;
                }
                
                field.setAccessible(true);
                
                Type fieldType = field.getGenericType();
                Object fieldValue = deserializeObject(fieldValueElement, fieldType);
                
                field.set(instance, fieldValue);

            } catch (NoSuchFieldException e) {
                // Field exists in JSON but not in class. Ignore it.
            } catch (Exception e) {
                System.err.println("Failed to set field " + fieldName + " on " + rawClass.getName() + ": " + e.getMessage());
            }
        }
        return instance;
    }

    private static Object deserializeUntyped(Json5Element element) {
        if (element == null || element.isJson5Null()) {
            return null;
        }
        if (element.isJson5Primitive()) {
            Json5Primitive p = element.getAsJson5Primitive();
            if (p.isBoolean()) return p.getAsBoolean();
            if (p.isNumber()) return p.getAsNumber();
            return p.getAsString();
        }
        if (element.isJson5Array()) {
            Json5Array arr = element.getAsJson5Array();
            List<Object> list = new ArrayList<>(arr.size());
            for (Json5Element item : arr) {
                list.add(deserializeUntyped(item));
            }
            return list;
        }
        if (element.isJson5Object()) {
            Json5Object obj = element.getAsJson5Object();
            Map<String, Object> map = new LinkedHashMap<>();
            for (Map.Entry<String, Json5Element> entry : obj.entrySet()) {
                map.put(entry.getKey(), deserializeUntyped(entry.getValue()));
            }
            return map;
        }
        return null; // Should be unreachable
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
        throw new IllegalArgumentException("Cannot determine raw class for Type: " + type);
    }

    private static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }

        throw new NoSuchFieldException("No field named '" + fieldName + "' found in " + clazz.getName() + " or its superclasses.");
    }
}
