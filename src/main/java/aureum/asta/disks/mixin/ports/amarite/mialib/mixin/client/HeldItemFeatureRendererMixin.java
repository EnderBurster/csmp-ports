package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.client;

import net.minecraft.util.Hand;
import net.minecraft.util.Arm;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({HeldItemFeatureRenderer.class})
public abstract class HeldItemFeatureRendererMixin {
   @Inject(
      method = {"renderItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void mialib$hideItem(
      LivingEntity entity,
      @NotNull ItemStack stack,
      ModelTransformationMode transformationMode,
      Arm arm,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      CallbackInfo ci
   ) {
      if (stack.getItem().mialib$shouldHideInHand(entity, entity.getMainArm() == arm ? Hand.MAIN_HAND : Hand.OFF_HAND, stack)) {
         ci.cancel();
      }
   }
}
