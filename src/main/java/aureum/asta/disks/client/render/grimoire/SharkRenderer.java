package aureum.asta.disks.client.render.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.entity.grimoire.SharkEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SharkRenderer extends GeoEntityRenderer<SharkEntity> {
    public SharkRenderer(EntityRendererFactory.Context renderManager) {
        super(renderManager, new SharkModel());
    }

    @Override
    public Identifier getTextureLocation(SharkEntity animatable)
    {
        return new Identifier(AureumAstaDisks.MOD_ID, "textures/entity/spectral_shark.png");
    }

    @Override
    public void render(SharkEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
