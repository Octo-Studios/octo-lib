package it.hurts.octostudios.octolib.util;

import net.minecraft.util.Mth;

import java.awt.*;

public class ColorUtils {

    public static Color blend(Color a, Color b, double percentA) {

        if (percentA < 0 || percentA > 1) {
            throw new IllegalArgumentException("Percent must be between 0 amd 1.");
        }

        int aRed = a.getRed();
        int aGreen = a.getGreen();
        int aBlue = a.getBlue();
        int aAlpha = a.getAlpha();

        int bRed = b.getRed();
        int bGreen = b.getGreen();
        int bBlue = b.getBlue();
        int bAlpha = b.getAlpha();

        int blendedRed = (int) ((1 - percentA) * aRed + percentA * bRed);
        int blendedGreen = (int) ((1 - percentA) * aGreen + percentA * bGreen);
        int blendedBlue = (int) ((1 - percentA) * aBlue + percentA * bBlue);
        int blendedAlpha = (int) ((1 - percentA) * aAlpha + percentA * bAlpha);

        return new Color(blendedRed, blendedGreen, blendedBlue, blendedAlpha);
    }

    public static Color add(Color c1, Color c2) {
        int r = clamp(c1.getRed()   + c2.getRed());
        int g = clamp(c1.getGreen() + c2.getGreen());
        int b = clamp(c1.getBlue()  + c2.getBlue());
        int a = clamp(c1.getAlpha() + c2.getAlpha());
        return new Color(r, g, b, a);
    }

    public static Color subtract(Color c1, Color c2) {
        int r = clamp(c1.getRed()   - c2.getRed());
        int g = clamp(c1.getGreen() - c2.getGreen());
        int b = clamp(c1.getBlue()  - c2.getBlue());
        int a = clamp(c1.getAlpha() - c2.getAlpha());
        return new Color(r, g, b, a);
    }

    private static int clamp(int value) {
        return Mth.clamp(value, 0, 255);
    }
}
