package aureum.asta.disks.mixin.ports.amarite.amarite.mixin.client;

import aureum.asta.disks.interfaces.Dualhanded;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import net.minecraft.util.Arm;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.DiscComponent;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

@Mixin({HeldItemFeatureRenderer.class})
public abstract class HeldItemFeatureRendererMixin<T extends LivingEntity, M extends EntityModel<T> & ModelWithArms> extends FeatureRenderer<T, M> {
   public HeldItemFeatureRendererMixin(FeatureRendererContext<T, M> context) {
      super(context);
   }

   @Inject(
      method = {"renderItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   protected void amarite$twoHanded(
      @NotNull LivingEntity entity,
      ItemStack stack,
      ModelTransformationMode transformationMode,
      Arm arm,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      CallbackInfo ci
   ) {
      if ((entity.getMainHandStack().isOf(AmariteItems.AMARITE_LONGSWORD) || entity.getMainHandStack().getItem() instanceof Dualhanded) && entity.getMainArm() != arm) {
         matrices.push();
         ((ModelWithArms)this.getContextModel()).setArmAngle(arm, matrices);
         matrices.pop();
         ci.cancel();
      }

      if (stack.isOf(AmariteItems.AMARITE_DISC) && entity instanceof PlayerEntity player) {
         DiscComponent discComponent = (DiscComponent)Amarite.DISC.get(player);
         boolean leftArm = arm == Arm.LEFT;

         for (int i = 0; i < (player.isCreative() ? 3 : 1) * 3; i++) {
            if (discComponent.getDiscEntity(i) == null && discComponent.getDiscDurability(i) > 0) {
               matrices.push();
               ((ModelWithArms)this.getContextModel()).setArmAngle(arm, matrices);
               matrices.scale(0.7F, 0.7F, 0.7F);
               matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternionf(-90.0F));
               matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternionf(180.0F));
               matrices.translate((double)((float)i * (leftArm ? -1.0F : 1.0F) / 12.0F), 0.135, -0.625);
               MinecraftClient.getInstance()
                  .getItemRenderer()
                  .renderItem(
                     entity,
                     stack,
                     transformationMode,
                     leftArm,
                     matrices,
                     vertexConsumers,
                     entity.world,
                     light,
                     OverlayTexture.DEFAULT_UV,
                     entity.getId() + transformationMode.ordinal()
                  );
               matrices.pop();
            }
         }

         ci.cancel();
      }
   }
}
