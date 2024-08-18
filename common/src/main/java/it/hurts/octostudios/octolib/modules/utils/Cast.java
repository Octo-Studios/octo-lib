package it.hurts.octostudios.octolib.modules.utils;

public class Cast {
    public static <T> T cast(final Object object) {
        if (object == null)
            return null;

        @SuppressWarnings("unchecked")
        final T type = (T) object;

        return type;
    }
}