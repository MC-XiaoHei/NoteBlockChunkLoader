package cn.xor7.xiaohei.note_block_chunk_loader.bridge;

import cn.xor7.xiaohei.note_block_chunk_loader.NoteBlockChunkLoader;
import cn.xor7.xiaohei.note_block_chunk_loader.mixin.bridge.EventBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.level.TicketType.ENDER_PEARL;

public class EventBridgeImpl implements EventBridge {
    @Override
    public void tryLoadChunk(@NotNull ServerLevel level, @NotNull BlockPos pos) {
        World world = level.getWorld();
        Block block = world.getBlockAt(pos.getX(), pos.getY(), pos.getZ());

        Block aboveBlock = block.getRelative(0, 1, 0);

        if (!checkAbove(aboveBlock)) return;
        Block belowBlock = block.getRelative(0, -1, 0);
        if (!checkBelow(belowBlock)) return;

        addTicket(level, new ChunkPos(pos), NoteBlockChunkLoader.plugin.getPluginConfig().loadRadius());
        if (NoteBlockChunkLoader.plugin.getPluginConfig().keepWorldTickUpdate()) {
            level.resetEmptyTime();
        }
    }

    private boolean checkAbove(Block aboveBlock) {
        Material loadWhenAboveBlockIs = NoteBlockChunkLoader.plugin.getPluginConfig().loadWhenAboveBlockIs();
        if (loadWhenAboveBlockIs == null) return true;
        return aboveBlock.getType().equals(loadWhenAboveBlockIs);
    }

    private boolean checkBelow(Block belowBlock) {
        Material loadWhenBelowBlockIs = NoteBlockChunkLoader.plugin.getPluginConfig().loadWhenBelowBlockIs();
        if (loadWhenBelowBlockIs == null) return true;
        return belowBlock.getType().equals(loadWhenBelowBlockIs);
    }

    private static void addTicket(@NotNull ServerLevel level, ChunkPos pos, int loadRadius) {
        ServerChunkCache chunkSource = level.getChunkSource();
        chunkSource.addTicketWithRadius(ENDER_PEARL, pos, loadRadius);
    }
}
