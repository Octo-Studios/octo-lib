package it.hurts.octostudios.octolib.util;

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

}
