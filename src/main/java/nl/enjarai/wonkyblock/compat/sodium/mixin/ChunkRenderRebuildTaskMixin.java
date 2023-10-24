package nl.enjarai.wonkyblock.compat.sodium.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import nl.enjarai.wonkyblock.WonkyBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkBuilderMeshingTask.class)
public abstract class ChunkRenderRebuildTaskMixin {
    @WrapWithCondition(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderer;renderModel(Lme/jellysquid/mods/sodium/client/render/chunk/compile/pipeline/BlockRenderContext;Lme/jellysquid/mods/sodium/client/render/chunk/compile/ChunkBuildBuffers;)V"
            ),
            remap = false
    )
    private boolean wonkyblock$hideBlock(BlockRenderer renderer, BlockRenderContext context, ChunkBuildBuffers buffers) {
        return !WonkyBlock.getInvisibleBlocks().contains(context.pos());
    }
}
