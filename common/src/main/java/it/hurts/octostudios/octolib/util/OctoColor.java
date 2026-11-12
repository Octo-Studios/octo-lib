package it.hurts.octostudios.octolib.util;

import java.util.Objects;

public final class OctoColor {
    public static final OctoColor RED = new OctoColor(1f, 0f, 0f, 1f);
    public static final OctoColor GREEN = new OctoColor(0f, 1f, 0f, 1f);
    public static final OctoColor BLUE = new OctoColor(0f, 0f, 1f, 1f);
    public static final OctoColor WHITE = new OctoColor(1f, 1f, 1f, 1f);
    public static final OctoColor BLACK = new OctoColor(0f, 0f, 0f, 1f);
    public static final OctoColor ZERO = new OctoColor(0f, 0f, 0f, 0f);

    private final float r;
    private final float g;
    private final float b;
    private final float a;

    public OctoColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public OctoColor(int argb) {
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

    public OctoColor lerp(OctoColor other, double t) {
        return new OctoColor(
                AnimationUtils.FLOAT.lerp(this.r, other.r, t),
                AnimationUtils.FLOAT.lerp(this.g, other.g, t),
                AnimationUtils.FLOAT.lerp(this.b, other.b, t),
                AnimationUtils.FLOAT.lerp(this.a, other.a, t)
        );
    }

    public static OctoColor lerp(double t, OctoColor... colors) {
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

    public OctoColor add(OctoColor other) {
        return new OctoColor(
                this.r + other.r,
                this.g + other.g,
                this.b + other.b,
                this.a + other.a
        );
    }

    public OctoColor subtract(OctoColor other) {
        return new OctoColor(
                this.r - other.r,
                this.g - other.g,
                this.b - other.b,
                this.a - other.a
        );
    }

    public OctoColor multiply(float rFactor, float gFactor, float bFactor, float aFactor) {
        return new OctoColor(
                this.r * rFactor,
                this.g * gFactor,
                this.b * bFactor,
                this.a * aFactor
        );
    }

    public OctoColor multiply(float factor) {
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
        var that = (OctoColor) obj;
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
        return "OctoColor[" +
                "r=" + r + ", " +
                "g=" + g + ", " +
                "b=" + b + ", " +
                "a=" + a + ']';
    }

}
