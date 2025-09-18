package it.hurts.shatterbyte.shatterlib.neoforge;

import it.hurts.shatterbyte.shatterlib.ShatterLibClient;
import net.neoforged.bus.api.IEventBus;

public final class ShatterLibNeoForgeClient {
    public ShatterLibNeoForgeClient(IEventBus modBus) {
        ShatterLibClient.init();
    }
}
