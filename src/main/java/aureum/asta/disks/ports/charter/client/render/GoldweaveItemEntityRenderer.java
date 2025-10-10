package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import aureum.asta.disks.ports.charter.common.item.GoldweaveItemEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class GoldweaveItemEntityRenderer extends EntityRenderer<GoldweaveItemEntity> {
   public final ItemRenderer itemRenderer;

   public GoldweaveItemEntityRenderer(Context context) {
      super(context);
      this.itemRenderer = context.getItemRenderer();
      this.shadowRadius = 0.0F;
      this.shadowOpacity = 0.0F;
   }

   public void render(
      GoldweaveItemEntity entityIn, float entityYaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider bufferIn, int packedLightIn
   ) {
      matrixStack.push();
      ItemStack itemstack = entityIn.getItem();
      BakedModel ibakedmodel = this.itemRenderer.getModel(itemstack, entityIn.world, null, 1);
      matrixStack.translate(0.0, 0.5, 0.0);
      matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternionf(entityYaw - 90.0F));
      matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternionf(MathHelper.lerp(tickDelta, entityIn.prevPitch, entityIn.getPitch()) - 45.0F));

      if (itemstack.getItem() == AmariteItems.AMARITE_LONGSWORD) {
         matrixStack.scale(1.85F, 1.85F, 1.0F);
      }
      else if (itemstack.getItem() == AstaItems.BLOOD_SCYTHE)
      {
         matrixStack.scale(1.5F, 1.5F, 1.0F);
      }

      this.itemRenderer.renderItem(itemstack, ModelTransformationMode.NONE, false, matrixStack, bufferIn, packedLightIn, OverlayTexture.DEFAULT_UV, ibakedmodel);
      matrixStack.pop();
      super.render(entityIn, entityYaw, tickDelta, matrixStack, bufferIn, packedLightIn);
   }

   public Identifier getTexture(GoldweaveItemEntity entity) {
      return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
   }
}
