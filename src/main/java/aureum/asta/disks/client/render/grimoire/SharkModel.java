package aureum.asta.disks.client.render.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.SharkEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SharkModel extends GeoModel<SharkEntity> {
    @Override
    public Identifier getModelResource(SharkEntity sharkEntity) {
        return new Identifier(AureumAstaDisks.MOD_ID, "geo/entity/spectral_shark.geo.json");
    }

    @Override
    public Identifier getTextureResource(SharkEntity sharkEntity) {
        return new Identifier(AureumAstaDisks.MOD_ID, "textures/entity/spectral_shark.png");
    }

    @Override
    public Identifier getAnimationResource(SharkEntity sharkEntity) {
        return new Identifier(AureumAstaDisks.MOD_ID, "animations/entity/spectral_shark.animation.json");
    }

    @Override
    public void setCustomAnimations(SharkEntity animatable, long instanceId, AnimationState<SharkEntity> animationState)
    {
        CoreGeoBone head = getAnimationProcessor().getBone("Head");

        if (head != null)
        {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }

    @Override
    public RenderLayer getRenderType(SharkEntity animatable, Identifier texture) {
        return RenderLayer.getEntityTranslucent(texture);
    }
}
