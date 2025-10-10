package aureum.asta.disks.mixin.ports.amarite.amarite.mixin.client;

import aureum.asta.disks.interfaces.Dualhanded;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.util.Hand;
import net.minecraft.item.ItemStack;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.DiscComponent;
import aureum.asta.disks.ports.amarite.amarite.cca.LongswordAccumulateComponent;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

@Mixin({PlayerEntityRenderer.class})
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
   public PlayerEntityRendererMixin(Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
      super(ctx, model, shadowRadius);
   }

   @Inject(
      method = {"getArmPose"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void amarite$swordPoses(@NotNull AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<ArmPose> cir) {
      ItemStack main = player.getMainHandStack();
      if (main.isOf(AmariteItems.AMARITE_LONGSWORD)) {
         boolean blocking = player.mialib$isUsing() || ((LongswordAccumulateComponent)Amarite.ACCUMULATE.get(player)).accumulateActive;
         if (hand != Hand.MAIN_HAND) {
            cir.setReturnValue(blocking ? ArmPose.BOW_AND_ARROW : ArmPose.CROSSBOW_CHARGE);
         } else {
            cir.setReturnValue(blocking ? ArmPose.BOW_AND_ARROW : ArmPose.BLOCK);
         }

         if (blocking) {
            player.bodyYaw = player.headYaw;
            player.prevBodyYaw = player.prevHeadYaw;
         }
      }
      else if(main.getItem() instanceof Dualhanded)
      {
         if (hand != Hand.MAIN_HAND) {
            cir.setReturnValue(ArmPose.CROSSBOW_CHARGE);
         } else {
            cir.setReturnValue(ArmPose.BLOCK);
         }
      }

      if (player.getStackInHand(hand).isOf(AmariteItems.AMARITE_DISC) && ((DiscComponent)Amarite.DISC.get(player)).getNextAvailableDisc() == -1) {
         cir.setReturnValue(ArmPose.EMPTY);
      }
   }

   /*@Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   public void amarite$backBlade(Context ctx, boolean slim, CallbackInfo ci) {
      this.addFeature(new AmariteLongswordFeatureRenderer(this));
   }*/
}
