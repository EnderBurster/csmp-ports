package aureum.asta.disks.client.render.entity;

import aureum.asta.disks.client.render.model.PoisonDartFrogEntityModel;
import aureum.asta.disks.entity.PoisonDartFrogEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class PoisonDartFrogEntityRenderer extends GeoEntityRenderer<PoisonDartFrogEntity> {
    public PoisonDartFrogEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new PoisonDartFrogEntityModel());
        this.shadowRadius = 0.35f;
    }

    @Override
    public RenderLayer getRenderType(PoisonDartFrogEntity animatable, Identifier texture, VertexConsumerProvider bufferSource, float partialTick) {
        return RenderLayer.getEntityTranslucent(this.getTexture(animatable));
    }
}
