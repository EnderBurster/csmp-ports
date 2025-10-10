package aureum.asta.disks.ports.amarite.amarite.client;

import net.minecraft.client.texture.SpriteAtlasTexture;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.entities.DiscEntity;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

public class DiscEntityRenderer extends EntityRenderer<DiscEntity> {
   private static final ItemStack stack = AmariteItems.AMARITE_DISC.getDefaultStack();
   private final ItemRenderer itemRenderer;

   public DiscEntityRenderer(Context ctx) {
      super(ctx);
      this.itemRenderer = ctx.getItemRenderer();
   }

   public void render(@NotNull DiscEntity disc, float yaw, float tickDelta, @NotNull MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
      matrices.push();
      matrices.translate(
         (disc.lastRenderX - disc.getX()) * (double)tickDelta + disc.getVelocity().x * (double)tickDelta,
         (disc.lastRenderY - disc.getY()) * (double)tickDelta + disc.getVelocity().y * (double)tickDelta,
         (disc.lastRenderZ - disc.getZ()) * (double)tickDelta + disc.getVelocity().z * (double)tickDelta
      );
      matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternionf(180.0F + MathHelper.lerp(tickDelta, disc.prevYaw, disc.getYaw())));
      matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternionf(90.0F + MathHelper.lerp(tickDelta, disc.prevPitch, disc.getPitch())));
      matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternionf(((float)disc.age + tickDelta) * -75.0F));
      matrices.scale(1.0F, 1.0F, 1.0F);

      this.itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, null, disc.getId());
      matrices.pop();
      super.render(disc, yaw, tickDelta, matrices, vertexConsumers, light);
   }

   public Identifier getTexture(DiscEntity entity) {
      return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
   }
}
