package aureum.asta.disks.ports.mace.client.model;

import aureum.asta.disks.ports.mace.entity.AbstractWindChargeEntity;
import aureum.asta.disks.ports.mace.entity.WindChargeEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class WindChargeEntityRenderer extends EntityRenderer<AbstractWindChargeEntity> {
   private static final Identifier TEXTURE = new Identifier("aureum-asta-disks", "textures/entity/projectiles/wind_charge.png");
   private final WindChargeEntityModel model;
   private static final RenderLayer WIND_CHARGE_LAYER;

   public WindChargeEntityRenderer(EntityRendererFactory.Context context) {
      super(context);
      this.model = new WindChargeEntityModel(context.getPart(WindChargeEntityModel.WIND_CHARGE_MODEL_LAYER));
   }

   public Identifier getTexture(AbstractWindChargeEntity entity) {
      return TEXTURE;
   }

   public void render(AbstractWindChargeEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      VertexConsumer vertexConsumer = vertexConsumers.getBuffer(WIND_CHARGE_LAYER);
      float doesntMatterNotUsed = 0.0F;
      this.model.setAngles((WindChargeEntity)entity, doesntMatterNotUsed, doesntMatterNotUsed, (float)entity.age + tickDelta, doesntMatterNotUsed, doesntMatterNotUsed);
      this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
      super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
   }

   protected float getXOffset(float tickDelta) {
      return tickDelta * 0.03F;
   }

   static {
      WIND_CHARGE_LAYER = RenderLayer.getEntityCutoutNoCull(TEXTURE);
   }
}
