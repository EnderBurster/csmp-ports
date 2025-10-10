package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.client;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({PlayerEntityRenderer.class})
public class PlayerEntityRendererMixin {
   @Inject(
      method = {"getArmPose"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void mialib$hideOrPose(@NotNull AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<ArmPose> cir) {
      ItemStack stack = player.getStackInHand(hand);
      if (stack.getItem().mialib$shouldHideInHand(player, hand, stack)) {
         cir.setReturnValue(ArmPose.EMPTY);
      } else {
         ArmPose pose = stack.getItem().mialib$pose(player, hand, stack);
         if (pose != null) {
            cir.setReturnValue(pose);
         }
      }
   }
}
