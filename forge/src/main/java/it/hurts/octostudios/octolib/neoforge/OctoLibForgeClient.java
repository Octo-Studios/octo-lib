package it.hurts.octostudios.octolib.neoforge;

import it.hurts.octostudios.octolib.OctoLibClient;
import net.minecraftforge.eventbus.api.IEventBus;

public class OctoLibForgeClient {
	public OctoLibForgeClient(IEventBus modBus) {
		OctoLibClient.init();
	}
}
