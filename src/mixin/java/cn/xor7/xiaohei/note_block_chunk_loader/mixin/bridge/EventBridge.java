package cn.xor7.xiaohei.note_block_chunk_loader.mixin.bridge;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public interface EventBridge {
    void tryLoadChunk(ServerLevel level, BlockPos pos);
}
