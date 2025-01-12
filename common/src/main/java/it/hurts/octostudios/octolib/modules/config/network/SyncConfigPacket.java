package it.hurts.octostudios.octolib.modules.config.network;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.OctoLib;
import it.hurts.octostudios.octolib.modules.config.ConfigManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SyncConfigPacket {
    private final String configPath;
    private final String configFile;

    public static final ResourceLocation ID = new ResourceLocation(OctoLib.MODID, "config_sync");

    public SyncConfigPacket(FriendlyByteBuf buf) {
        this.configPath = buf.readUtf();
        this.configFile = buf.readUtf();
    }

    public SyncConfigPacket(String configPath) {
        this.configPath = configPath;
        this.configFile = ConfigManager.saveAsString(configPath);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(configPath);
        buf.writeUtf(configFile);
    }

    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            ConfigManager.reloadStringConfig(configFile, configPath, false);
        });
    }
}