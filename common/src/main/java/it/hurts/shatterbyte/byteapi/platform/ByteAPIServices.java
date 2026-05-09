package it.hurts.shatterbyte.byteapi.platform;

public final class ByteAPIServices {
    private static ByteAPIPlatform platform;

    private ByteAPIServices() {
    }

    public static void initialize(ByteAPIPlatform instance) {
        if (platform == null) {
            platform = instance;
        }
    }

    public static ByteAPIPlatform platform() {
        if (platform == null) {
            throw new IllegalStateException("ByteAPI platform service has not been initialized");
        }

        return platform;
    }
}
