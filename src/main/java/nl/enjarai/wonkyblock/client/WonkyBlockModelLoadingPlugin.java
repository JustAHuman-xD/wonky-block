package nl.enjarai.wonkyblock.client;

import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class WonkyBlockModelLoadingPlugin implements PreparableModelLoadingPlugin<Identifier> {
    @Override
    public void onInitializeModelLoader(Identifier identifier, ModelLoadingPlugin.Context context) {
        context.addModels(identifier);
    }

    public static class ModelLoader implements PreparableModelLoadingPlugin.DataLoader<Identifier> {
        @Override
        public CompletableFuture<Identifier> load(ResourceManager manager, Executor executor) {
            return CompletableFuture.supplyAsync(() -> new Identifier("minecraft", "block/wonky_block"));
        }
    }
}
