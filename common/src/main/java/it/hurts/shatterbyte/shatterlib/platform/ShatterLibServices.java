package it.hurts.shatterbyte.shatterlib.platform;

public final class ShatterLibServices {
    private static ShatterLibPlatform platform;

    private ShatterLibServices() {
    }

    public static void initialize(ShatterLibPlatform instance) {
        if (platform == null) {
            platform = instance;
        }
    }

    public static ShatterLibPlatform platform() {
        if (platform == null) {
            throw new IllegalStateException("ByteAPI platform service has not been initialized");
        }

        return platform;
    }
}
