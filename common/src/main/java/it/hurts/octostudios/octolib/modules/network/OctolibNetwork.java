package it.hurts.octostudios.octolib.modules.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.modules.config.network.SyncConfigPacket;

public class OctolibNetwork {
    
    private static final SimpleNetworkManager NET = SimpleNetworkManager.create(OctoLib.MODID);
    
    public static final MessageType SYNC_SHOP = NET.registerS2C("sync_config", SyncConfigPacket::new);
    
}
