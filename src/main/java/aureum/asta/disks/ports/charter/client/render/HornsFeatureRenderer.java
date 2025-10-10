package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.ports.charter.CharterClient;
import aureum.asta.disks.ports.charter.client.model.HornsModel;
import aureum.asta.disks.ports.charter.client.model.OrnateHornsModel;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class HornsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
   private static HornsModel MODEL;

   public HornsFeatureRenderer(
      FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, EntityModelLoader loader
   ) {
      super(context);
      MODEL = new OrnateHornsModel(loader.getModelPart(CharterClient.ORNATE_HORNS_MODEL_LAYER));
   }

   public void render(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      AbstractClientPlayerEntity player,
      float limbAngle,
      float limbDistance,
      float tickDelta,
      float animationProgress,
      float headYaw,
      float headPitch
   ) {
      if (!player.isInvisible()
         && player.getUuid().equals(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2"))
         && ((PlayerEntityModel)this.getContextModel()).head.visible) {
         matrices.push();
         ((PlayerEntityModel)this.getContextModel()).head.rotate(matrices);
         float g = 1.0F;
         float b = 1.0F;
         float a = 1.0F;
         MODEL.render(
            matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(MODEL.getTexture())), light, OverlayTexture.DEFAULT_UV, 1.0F, g, b, a
         );
         matrices.pop();
      }
   }
}
