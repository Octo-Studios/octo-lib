package it.hurts.shatterbyte.shatterlib.util;

import java.util.Objects;

public final class ShatterColor {
    public static final ShatterColor RED = new ShatterColor(1f, 0f, 0f, 1f);
    public static final ShatterColor GREEN = new ShatterColor(0f, 1f, 0f, 1f);
    public static final ShatterColor BLUE = new ShatterColor(0f, 0f, 1f, 1f);
    public static final ShatterColor WHITE = new ShatterColor(1f, 1f, 1f, 1f);
    public static final ShatterColor BLACK = new ShatterColor(0f, 0f, 0f, 1f);
    public static final ShatterColor ZERO = new ShatterColor(0f, 0f, 0f, 0f);

    private final float r;
    private final float g;
    private final float b;
    private final float a;

    public ShatterColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public ShatterColor(int argb) {
        this.a = ((argb >> 24) & 0xFF) / 255f;
        this.r = ((argb >> 16) & 0xFF) / 255f;
        this.g = ((argb >> 8) & 0xFF) / 255f;
        this.b = (argb & 0xFF) / 255f;
    }

    public int getARGB() {
        return ((((int) (a * 255)) & 0xFF) << 24) |
                ((((int) (r * 255)) & 0xFF) << 16) |
                ((((int) (g * 255)) & 0xFF) << 8) |
                ((((int) (b * 255)) & 0xFF));
    }

    public ShatterColor lerp(ShatterColor other, double t) {
        return new ShatterColor(
                AnimationUtils.FLOAT.lerp(this.r, other.r, t),
                AnimationUtils.FLOAT.lerp(this.g, other.g, t),
                AnimationUtils.FLOAT.lerp(this.b, other.b, t),
                AnimationUtils.FLOAT.lerp(this.a, other.a, t)
        );
    }

    public static ShatterColor lerp(double t, ShatterColor... colors) {
        if (colors == null || colors.length == 0) {
            throw new IllegalArgumentException("colors array can't be empty");
        }

        if (colors.length == 1) {
            return colors[0];
        }

        t = Math.min(Math.max(t, 0.0), 1.0);

        double scaledT = t * (colors.length - 1);
        int index = (int) Math.floor(scaledT);
        int nextIndex = Math.min(index + 1, colors.length - 1);

        double localT = scaledT - index;

        return colors[index].lerp(colors[nextIndex], localT);
    }

    public ShatterColor add(ShatterColor other) {
        return new ShatterColor(
                this.r + other.r,
                this.g + other.g,
                this.b + other.b,
                this.a + other.a
        );
    }

    public ShatterColor subtract(ShatterColor other) {
        return new ShatterColor(
                this.r - other.r,
                this.g - other.g,
                this.b - other.b,
                this.a - other.a
        );
    }

    public ShatterColor multiply(float rFactor, float gFactor, float bFactor, float aFactor) {
        return new ShatterColor(
                this.r * rFactor,
                this.g * gFactor,
                this.b * bFactor,
                this.a * aFactor
        );
    }

    public ShatterColor multiply(float factor) {
        return this.multiply(factor, factor, factor, factor);
    }

    public float r() {
        return r;
    }

    public float g() {
        return g;
    }

    public float b() {
        return b;
    }

    public float a() {
        return a;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ShatterColor) obj;
        return Float.floatToIntBits(this.r) == Float.floatToIntBits(that.r) &&
                Float.floatToIntBits(this.g) == Float.floatToIntBits(that.g) &&
                Float.floatToIntBits(this.b) == Float.floatToIntBits(that.b) &&
                Float.floatToIntBits(this.a) == Float.floatToIntBits(that.a);
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, a);
    }

    @Override
    public String toString() {
        return "ShatterColor[" +
                "r=" + r + ", " +
                "g=" + g + ", " +
                "b=" + b + ", " +
                "a=" + a + ']';
    }

}
