package it.hurts.shatterbyte.byteapi.util;

import it.hurts.shatterbyte.byteapi.client.animation.easing.Interpolator;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector2d;
import org.joml.Vector2f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BinaryOperator;

public class AnimationUtils {
    private static final Map<Class<?>, Interpolator<?>> INTERPOLATOR_REGISTRY = new HashMap<>();

    public static final Interpolator<Double> DOUBLE = register(Double.class, (from, to, t) -> from + (to - from) * t);
    public static final Interpolator<Float> FLOAT = register(Float.class, (from, to, t) -> from + (to - from) * (float) t);
    public static final Interpolator<ShatterColor> COLOR = register(ShatterColor.class, ShatterColor::lerp);
    public static final Interpolator<Vec2> VEC2 = register(Vec2.class, (from, to, t) -> new Vec2(
            FLOAT.lerp(from.x, to.x, t),
            FLOAT.lerp(from.y, to.y, t)
    ));
    public static final Interpolator<Vec3> VEC3 = register(Vec3.class, Vec3::lerp);
    public static final Interpolator<Vector2d> VECTOR2D = register(Vector2d.class, Vector2d::lerp);
    public static final Interpolator<Vector3d> VECTOR3D = register(Vector3d.class, Vector3d::lerp);
    public static final Interpolator<Vector2f> VECTOR2F = register(Vector2f.class, (from, to, t) -> new Vector2f(
            FLOAT.lerp(from.x, to.x, t),
            FLOAT.lerp(from.y, to.y, t)
    ));
    public static final Interpolator<Vector3f> VECTOR3F = register(Vector3f.class, (from, to, t) -> new Vector3f(
            FLOAT.lerp(from.x, to.x, t),
            FLOAT.lerp(from.y, to.y, t),
            FLOAT.lerp(from.z, to.z, t)
    ));

    private static final Map<Class<?>, BinaryOperator<?>> ADD_REGISTRY = new HashMap<>();
    private static final Map<Class<?>, BinaryOperator<?>> SUBTRACT_REGISTRY = new HashMap<>();

    static {
        registerAdd(Double.class, Double::sum);
        registerSubtract(Double.class, (a, b) -> a - b);

        registerAdd(Float.class, Float::sum);
        registerSubtract(Float.class, (a, b) -> a - b);

        registerAdd(ShatterColor.class, ShatterColor::add);
        registerSubtract(ShatterColor.class, ShatterColor::subtract);

        registerAdd(Vec2.class, (a, b) -> new Vec2(a.x + b.x, a.y + b.y));
        registerSubtract(Vec2.class, (a, b) -> new Vec2(a.x - b.x, a.y - b.y));

        registerAdd(Vec3.class, Vec3::add);
        registerSubtract(Vec3.class, Vec3::subtract);

        registerAdd(Vector2d.class, (a, b) -> new Vector2d(a.x + b.x, a.y + b.y));
        registerSubtract(Vector2d.class, (a, b) -> new Vector2d(a.x - b.x, a.y - b.y));

        registerAdd(Vector3d.class, (a, b) -> new Vector3d(a.x + b.x, a.y + b.y, a.z + b.z));
        registerSubtract(Vector3d.class, (a, b) -> new Vector3d(a.x - b.x, a.y - b.y, a.z - b.z));

        registerAdd(Vector2f.class, (a, b) -> new Vector2f(a.x + b.x, a.y + b.y));
        registerSubtract(Vector2f.class, (a, b) -> new Vector2f(a.x - b.x, a.y - b.y));

        registerAdd(Vector3f.class, (a, b) -> new Vector3f(a.x + b.x, a.y + b.y, a.z + b.z));
        registerSubtract(Vector3f.class, (a, b) -> new Vector3f(a.x - b.x, a.y - b.y, a.z - b.z));
    }

    @SuppressWarnings("unchecked")
    public static <T> T lerp(T from, T to, double t) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("Lerp arguments cannot be null");
        }

        // get the runtime classes of each argument
        Class<?> classFrom = from.getClass();
        Class<?> classTo = to.getClass();

        // if they’re not the same, check for numbers
        if (!classFrom.equals(classTo)) {
            if (isNum(from) && isNum(to)) {
                // pick the “higher” type: if either is Double, use Double; else Float
                Class<? extends Number> numClass = getCompatibleNumberType((Number) from, (Number) to);

                if (numClass == Double.class) {
                    // convert both to Double
                    double dFrom = ((Number) from).doubleValue();
                    double dTo   = ((Number) to).doubleValue();
                    // call the DOUBLE interpolator directly
                    Double result = DOUBLE.lerp(dFrom, dTo, t);
                    return (T) result; // safe because T is effectively Double here
                } else {
                    // must be Float.class
                    float fFrom = ((Number) from).floatValue();
                    float fTo   = ((Number) to).floatValue();
                    Float result = FLOAT.lerp(fFrom, fTo, t);
                    return (T) result; // safe because T is Float
                }
            } else {
                throw new IllegalArgumentException("Lerp types must match");
            }
        }

        // at this point, classFrom == classTo
        Interpolator<T> interpolator = (Interpolator<T>) INTERPOLATOR_REGISTRY.get(classFrom);
        if (interpolator == null) {
            throw new IllegalArgumentException("No interpolator registered for " + classFrom.getName());
        }
        return interpolator.lerp(from, to, t);
    }

    public static <T> T add(T first, T second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Add arguments cannot be null");
        }
        Class<?> classFirst = first.getClass();
        Class<?> classSecond = second.getClass();

        // mismatch: if both are Number, pick compatible numeric class
        if (!classFirst.equals(classSecond)) {
            if (isNum(first) && isNum(second)) {
                Class<? extends Number> numClass = getCompatibleNumberType((Number) first, (Number) second);
                if (numClass == Double.class) {
                    double a = ((Number) first).doubleValue();
                    double b = ((Number) second).doubleValue();
                    @SuppressWarnings("unchecked")
                    T result = (T) Double.valueOf(a + b);
                    return result;
                } else {
                    float a = ((Number) first).floatValue();
                    float b = ((Number) second).floatValue();
                    @SuppressWarnings("unchecked")
                    T result = (T) Float.valueOf(a + b);
                    return result;
                }
            } else {
                throw new IllegalArgumentException("Add types must match or both be numbers");
            }
        }

        @SuppressWarnings("unchecked")
        BinaryOperator<T> op = (BinaryOperator<T>) ADD_REGISTRY.get(classFirst);
        if (op == null) {
            throw new IllegalArgumentException("No add operation registered for " + classFirst.getName());
        }
        return op.apply(first, second);
    }

    // --- new: generic subtract ---
    public static <T> T subtract(T first, T second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("Subtract arguments cannot be null");
        }
        Class<?> classFirst = first.getClass();
        Class<?> classSecond = second.getClass();

        // mismatch: if both are Number, pick compatible numeric class
        if (!classFirst.equals(classSecond)) {
            if (isNum(first) && isNum(second)) {
                Class<? extends Number> numClass = getCompatibleNumberType((Number) first, (Number) second);
                if (numClass == Double.class) {
                    double a = ((Number) first).doubleValue();
                    double b = ((Number) second).doubleValue();
                    @SuppressWarnings("unchecked")
                    T result = (T) Double.valueOf(a - b);
                    return result;
                } else {
                    float a = ((Number) first).floatValue();
                    float b = ((Number) second).floatValue();
                    @SuppressWarnings("unchecked")
                    T result = (T) Float.valueOf(a - b);
                    return result;
                }
            } else {
                throw new IllegalArgumentException("Subtract types must match or both be numbers");
            }
        }

        @SuppressWarnings("unchecked")
        BinaryOperator<T> op = (BinaryOperator<T>) SUBTRACT_REGISTRY.get(classFirst);
        if (op == null) {
            throw new IllegalArgumentException("No subtract operation registered for " + classFirst.getName());
        }
        return op.apply(first, second);
    }

    // helper to register add op
    private static <T> void registerAdd(Class<T> clazz, BinaryOperator<T> op) {
        ADD_REGISTRY.put(clazz, op);
    }

    // helper to register subtract op
    private static <T> void registerSubtract(Class<T> clazz, BinaryOperator<T> op) {
        SUBTRACT_REGISTRY.put(clazz, op);
    }

    private static <T> Interpolator<T> register(Class<T> clazz, Interpolator<T> interpolator) {
        INTERPOLATOR_REGISTRY.put(clazz, interpolator);
        return interpolator;
    }

    private static boolean isNum(Object obj) {
        return obj instanceof Number;
    }

    private static Class<? extends Number> getCompatibleNumberType(Number first, Number second) {
        boolean isDoublePresent = first instanceof Double || second instanceof Double;
        return isDoublePresent ? Double.class : Float.class;
    }
}
