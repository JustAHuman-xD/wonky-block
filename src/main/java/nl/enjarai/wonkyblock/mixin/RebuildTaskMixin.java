package nl.enjarai.wonkyblock.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChunkBuilder.BuiltChunk.RebuildTask.class)
public abstract class RebuildTaskMixin {
    @WrapWithCondition(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/block/BlockRenderManager;renderBlock(Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/BlockRenderView;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;ZLnet/minecraft/util/math/random/Random;)V"
            )
    )
    private boolean wonkyblock$hideBlock() {
        return true;
    }
}
