package nl.enjarai.wonkyblock.compat.sodium.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildContext;
import me.jellysquid.mods.sodium.client.render.chunk.compile.tasks.ChunkBuilderMeshingTask;
import me.jellysquid.mods.sodium.client.util.task.CancellationToken;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(ChunkBuilderMeshingTask.class)
public abstract class ChunkRenderRebuildTaskMixin {

}
