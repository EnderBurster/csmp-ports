package aureum.asta.disks.ports.amarite.amarite.compat;

import aureum.asta.disks.cca.BackWeaponComponent;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

public class AmariteLongswordFeatureRenderer<T extends PlayerEntity, M extends EntityModel<T>> extends FeatureRenderer<T, M> {
   public AmariteLongswordFeatureRenderer(FeatureRendererContext<T, M> context) {
      super(context);
   }

   public void render(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      T entity,
      float limbAngle,
      float limbDistance,
      float tickDelta,
      float animationProgress,
      float headYaw,
      float headPitch
   ) {
      if (!BackWeaponComponent.isHoldingBackWeapon(entity)) {
         ItemStack stack = BackWeaponComponent.getBackWeapon(entity);
         if (!stack.isEmpty()) {
            if (stack.getItem() == AmariteItems.AMARITE_LONGSWORD) {
               matrices.push();
               matrices.translate(-0.1, 0.25, 0.275);
               matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternionf(0.0F));
               matrices.scale(1.85F, 1.85F, 1.0F);
               MinecraftClient.getInstance()
                  .getItemRenderer()
                  .renderItem(entity, stack, ModelTransformationMode.FIXED, false, matrices, vertexConsumers, entity.world, light, OverlayTexture.DEFAULT_UV, 0);
               matrices.pop();
            }
         }
      }
   }
}
