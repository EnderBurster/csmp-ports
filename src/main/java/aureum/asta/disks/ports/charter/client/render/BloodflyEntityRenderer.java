package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.ports.charter.CharterClient;
import aureum.asta.disks.ports.charter.client.model.BloodflyEntityModel;
import aureum.asta.disks.ports.charter.common.entity.living.BloodflyEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class BloodflyEntityRenderer extends MobEntityRenderer<BloodflyEntity, BloodflyEntityModel> {
   public static final Identifier TEXTURE = new Identifier("charter", "textures/entity/bloodfly.png");

   public BloodflyEntityRenderer(Context context) {
      super(context, new BloodflyEntityModel(context.getPart(CharterClient.BLOODFLY_MODEL_LAYER)), 0.25F);
      this.shadowRadius = 0.25F;
      this.shadowOpacity = 0.5F;
   }

   public void render(BloodflyEntity entity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      matrixStack.scale(1.2F, 1.2F, 1.2F);
      super.render(entity, f, g, matrixStack, vertexConsumerProvider, i);
   }

   public Identifier getTexture(BloodflyEntity entity) {
      return TEXTURE;
   }
}
