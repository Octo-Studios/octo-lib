package it.hurts.octostudios.octolib.module.chromatic_aberration;

import dev.architectury.networking.NetworkManager;
import it.hurts.octostudios.octolib.module.chromatic_aberration.misc.S2CChromaticAberrationPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChromaticAberrationManager {
    public static final Map<UUID, ChromaticAberration> CHROMATIC_ABERRATIONS = new HashMap<>();

    public static void add(Level level, ChromaticAberration chromaticAberration) {
        if (level.isClientSide()) {
            CHROMATIC_ABERRATIONS.put(chromaticAberration.getUuid(), chromaticAberration);

            return;
        }

        var vec = chromaticAberration.getAnchor().getPosition(level);

        var serverLevel = (ServerLevel) level;

        NetworkManager.sendToPlayers(serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(new BlockPos((int) vec.x(), (int) vec.y(), (int) vec.z())), false), new S2CChromaticAberrationPacket(chromaticAberration));
    }

    public static void addForPlayer(Player player, ChromaticAberration chromaticAberration) {
        if (player.level().isClientSide()) {
            CHROMATIC_ABERRATIONS.put(chromaticAberration.getUuid(), chromaticAberration);

            return;
        }

        NetworkManager.sendToPlayer((ServerPlayer) player, new S2CChromaticAberrationPacket(chromaticAberration));
    }
}