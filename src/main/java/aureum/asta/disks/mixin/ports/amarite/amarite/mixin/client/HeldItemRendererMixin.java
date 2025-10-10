package aureum.asta.disks.mixin.ports.amarite.amarite.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.item.HeldItemRenderer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.DiscComponent;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

@Mixin({HeldItemRenderer.class})
public abstract class HeldItemRendererMixin {
   @WrapOperation(
      method = {"renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;Lnet/minecraft/client/network/ClientPlayerEntity;I)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
      )}
   )
   public void amarite$renderItems(
      HeldItemRenderer renderer,
      @NotNull AbstractClientPlayerEntity player,
      float tickDelta,
      float pitch,
      Hand hand,
      float swingProgress,
      ItemStack item,
      float equipProgress,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      Operation<Void> operation
   ) {
      ItemStack stack = player.getStackInHand(hand);
      if (stack.isOf(AmariteItems.AMARITE_DISC)) {
         DiscComponent discComponent = (DiscComponent)Amarite.DISC.get(player);

         for (int i = 0; i < (player.isCreative() ? 3 : 1) * 3; i++) {
            if (discComponent.getDiscEntity(i) == null && discComponent.getDiscDurability(i) > 0) {
               matrices.push();
               matrices.translate(0.12 * (double)(i - 1), 0.1, 0.0);
               operation.call(renderer, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
               matrices.pop();
            }
         }
      } else {
         operation.call(renderer, player, tickDelta, pitch, hand, swingProgress, item, equipProgress, matrices, vertexConsumers, light);
      }
   }

   @Inject(
      method = {"resetEquipProgress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void amarite$longswordPickup(Hand hand, CallbackInfo ci) {
      if (MinecraftClient.getInstance().player != null) {
         ItemStack stack = MinecraftClient.getInstance().player.getStackInHand(hand);
         if (stack.isOf(AmariteItems.AMARITE_LONGSWORD)) {
            ci.cancel();
         }
      }
   }
}
