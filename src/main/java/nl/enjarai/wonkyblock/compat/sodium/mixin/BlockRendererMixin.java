package nl.enjarai.wonkyblock.compat.sodium.mixin;

import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import nl.enjarai.wonkyblock.WonkyBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderer.class)
public abstract class BlockRendererMixin {
    @Inject(
            method = "renderModel",
            at = @At(value = "HEAD"),
            cancellable = true,
            remap = false
    )
    private void wonkyblock$hideBlock(BlockRenderContext ctx, ChunkBuildBuffers buffers, CallbackInfo ci) {
        if (WonkyBlock.getInvisibleBlocks().contains(ctx.pos())) {
            ci.cancel();
        }
    }
}
