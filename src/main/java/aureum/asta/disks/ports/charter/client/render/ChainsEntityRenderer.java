package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.ports.charter.CharterClient;
import aureum.asta.disks.ports.charter.client.model.ChainsEntityModel;
import aureum.asta.disks.ports.charter.common.entity.ChainsEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class ChainsEntityRenderer extends EntityRenderer<ChainsEntity> {
   public static final Identifier TEXTURE = new Identifier("charter", "textures/entity/chains.png");
   public ChainsEntityModel model;

   public ChainsEntityRenderer(Context context) {
      super(context);
      this.shadowRadius = 0.0F;
      this.shadowOpacity = 0.0F;
      this.model = new ChainsEntityModel(context.getPart(CharterClient.CHAINS_MODEL_LAYER));
   }

   public void render(ChainsEntity chains, float f, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int i) {
      matrixStack.push();
      this.model.animateModel(chains, 0.0F, 0.0F, tickDelta);
      matrixStack.translate(0.0, 0.15F, 0.0);
      float h = MathHelper.clamp(0.9F + 1.0F / (((float)chains.age + tickDelta + 0.01F) * 0.2F), 0.0F, 5.0F);
      matrixStack.scale(h, h, h);
      this.model.render(matrixStack, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE)), 15728880, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
      matrixStack.pop();
      super.render(chains, f, tickDelta, matrixStack, vertexConsumers, i);
   }

   public Identifier getTexture(ChainsEntity entity) {
      return TEXTURE;
   }
}
