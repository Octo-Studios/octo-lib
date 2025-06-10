package it.hurts.octostudios.octolib.util;

import lombok.Getter;

@Getter
public class OctoColor {
    public static final OctoColor RED = new OctoColor(1f,0f,0f,1f);
    public static final OctoColor GREEN = new OctoColor(0f,1f,0f,1f);
    public static final OctoColor BLUE = new OctoColor(0f,0f,1f,1f);
    public static final OctoColor WHITE = new OctoColor(1f,1f,1f,1f);
    public static final OctoColor BLACK = new OctoColor(0f,0f,0f,1f);
    public static final OctoColor ZERO = new OctoColor(0f,0f,0f,0f);

    public float r;
    public float g;
    public float b;
    public float a;

    public OctoColor(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
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
}
