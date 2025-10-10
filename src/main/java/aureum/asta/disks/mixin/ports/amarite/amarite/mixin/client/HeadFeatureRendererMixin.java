package aureum.asta.disks.mixin.ports.amarite.amarite.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import aureum.asta.disks.ports.amarite.amarite.items.MaskItem;

@Mixin({HeadFeatureRenderer.class})
public class HeadFeatureRendererMixin<T extends LivingEntity> {
   @WrapOperation(
      method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/LivingEntity;getEquippedStack(Lnet/minecraft/entity/EquipmentSlot;)Lnet/minecraft/item/ItemStack;"
      )}
   )
   private ItemStack amarite$hideHeadFeature(LivingEntity entity, EquipmentSlot slot, Operation<ItemStack> original) {
      ItemStack mask = MaskItem.getWornMask(entity);
      return !mask.isEmpty() ? mask : (ItemStack)original.call(entity, slot);
   }

   @Inject(
      method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
      )},
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   private void amarite$maskOffset(
      MatrixStack matrixStack,
      VertexConsumerProvider vertexConsumerProvider,
      int i,
      T livingEntity,
      float f,
      float g,
      float h,
      float j,
      float k,
      float l,
      CallbackInfo ci,
      @NotNull ItemStack stack
   ) {
      if (stack.getItem() instanceof MaskItem) {
         matrixStack.translate(0.0, (double)MaskItem.getOffset(stack) * 0.1, 0.0);
      }
   }
}
