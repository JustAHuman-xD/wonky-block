package nl.enjarai.wonkyblock.mixin;

import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.util.Identifier;
import nl.enjarai.wonkyblock.accessor.BakedModelManagerAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Map;

@Mixin(BakedModelManager.class)
public abstract class BakedModelManagerMixin implements BakedModelManagerAccess {
    @Shadow private Map<Identifier, BakedModel> models;

    @Shadow
    public abstract BakedModel getMissingModel();

    @Override @Unique
    public BakedModel reallyGetModel(Identifier model) {
        return models.getOrDefault(model, this.getMissingModel());
    }
}
