package it.hurts.shatterbyte.shatterlib.module.camera_shake;

import it.hurts.shatterbyte.shatterlib.module.network.ShatterLibNetwork;
import it.hurts.shatterbyte.shatterlib.module.camera_shake.misc.S2CCameraShakePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CameraShakeManager {
    public static final Map<UUID, CameraShake> SHAKES = new HashMap<>();

    public static void add(Level level, CameraShake shake) {
        if (level.isClientSide()) {
            SHAKES.put(shake.getUuid(), shake);

            return;
        }

        var vec = shake.getAnchor().getPosition(level);

        var serverLevel = (ServerLevel) level;

        var chunkPos = ChunkPos.containing(new BlockPos((int) vec.x(), (int) vec.y(), (int) vec.z()));

        ShatterLibNetwork.sendToPlayers(serverLevel.getChunkSource().chunkMap.getPlayers(chunkPos, false), new S2CCameraShakePacket(shake));
    }

    public static void addForPlayer(Player player, CameraShake shake) {
        if (player.level().isClientSide()) {
            SHAKES.put(shake.getUuid(), shake);

            return;
        }

        ShatterLibNetwork.sendToPlayer((ServerPlayer) player, new S2CCameraShakePacket(shake));
    }
}
