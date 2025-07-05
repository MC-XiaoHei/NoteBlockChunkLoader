package cn.xor7.xiaohei.note_block_chunk_loader.mixin.mixins;

import cn.xor7.xiaohei.note_block_chunk_loader.mixin.bridge.EventBridgeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoteBlock.class)
public abstract class NoteBlockMixin {
    @Inject(method = "playNote", at = @At("HEAD"))
    private void onPlayNote(Entity entity, BlockState state, Level level, BlockPos pos, CallbackInfo ci) {
        EventBridgeManager.INSTANCE.getBridge().tryLoadChunk((ServerLevel) level, pos);
    }
}