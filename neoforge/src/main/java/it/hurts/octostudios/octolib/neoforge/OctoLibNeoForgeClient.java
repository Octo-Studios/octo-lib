package it.hurts.octostudios.octolib.neoforge;

import it.hurts.octostudios.octolib.OctoLibClient;
import net.neoforged.bus.api.IEventBus;

public final class OctoLibNeoForgeClient {
    public OctoLibNeoForgeClient(IEventBus modBus) {
        OctoLibClient.init();
    }
}
