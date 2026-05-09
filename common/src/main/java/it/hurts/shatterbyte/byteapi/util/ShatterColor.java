package it.hurts.shatterbyte.byteapi.util;

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

    public ShatterColor(float red, float green, float blue, float alpha) {
        this.r = red;
        this.g = green;
        this.b = blue;
        this.a = alpha;
    }

    public static ShatterColor fromHSV(float hue, float saturation, float value, float alpha) {
        hue = hue % 1.0f;
        if (hue < 0) hue += 1.0f;

        saturation = Math.min(Math.max(saturation, 0f), 1f);
        value = Math.min(Math.max(value, 0f), 1f);

        float tempR = 0f, tempG = 0f, tempB = 0f;

        if (saturation == 0.0f) {
            tempR = tempG = tempB = value;
        } else {
            float i = (float) Math.floor(hue * 6.0f);
            float f = hue * 6.0f - i;
            float p = value * (1.0f - saturation);
            float q = value * (1.0f - saturation * f);
            float t = value * (1.0f - saturation * (1.0f - f));

            int sector = (int) i % 6;

            switch (sector) {
                case 0: tempR = value; tempG = t; tempB = p; break;
                case 1: tempR = q; tempG = value; tempB = p; break;
                case 2: tempR = p; tempG = value; tempB = t; break;
                case 3: tempR = p; tempG = q; tempB = value; break;
                case 4: tempR = t; tempG = p; tempB = value; break;
                case 5: tempR = value; tempG = p; tempB = q; break;
            }
        }

        return new ShatterColor(tempR, tempG, tempB, alpha);
    }

    /**
     * Converts the RGB components to HSV (Hue, Saturation, Value).
     * @return A float array {hue, saturation, value}, where each component is in the range [0.0, 1.0].
     */
    public float[] toHSV() {
        float min = Math.min(r, Math.min(g, b));
        float max = Math.max(r, Math.max(g, b));
        float delta = max - min;

        float h, s, v;

        v = max;

        if (max != 0) {
            s = delta / max;
        } else {
            s = 0;
            h = 0;
            return new float[] {h, s, v};
        }

        if (delta == 0) {
            h = 0;
        } else if (r == max) {
            h = (g - b) / delta;
        } else if (g == max) {
            h = 2f + (b - r) / delta;
        } else {
            h = 4f + (r - g) / delta;
        }

        h /= 6.0f;
        if (h < 0) {
            h += 1.0f;
        }

        return new float[] {h, s, v};
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
