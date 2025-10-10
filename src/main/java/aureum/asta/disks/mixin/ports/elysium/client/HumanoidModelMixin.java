package aureum.asta.disks.mixin.ports.elysium.client;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.client.cheirosiphon.CheirosiphonItemRenderer;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({BipedEntityModel.class})
public class HumanoidModelMixin<T extends LivingEntity> {
   @Shadow
   @Final
   public ModelPart leftArm;
   @Shadow
   @Final
   public ModelPart head;
   @Shadow
   @Final
   public ModelPart rightArm;

   @Inject(
      method = {"positionLeftArm"},
      at = {@At("TAIL")}
   )
   private void elysium$poseLeftArmForCheirosiphon(T livingEntity, CallbackInfo ci) {
      if (livingEntity.getMainHandStack().isOf(Elysium.CHEIROSIPHON)) {
         CheirosiphonItemRenderer.animateHold(this.rightArm, this.leftArm, this.head, livingEntity.getMainArm() == Arm.RIGHT);
      } else if (livingEntity.getOffHandStack().isOf(Elysium.CHEIROSIPHON)) {
         CheirosiphonItemRenderer.animateOffhandHold(this.rightArm, this.leftArm, this.head, livingEntity.getMainArm() == Arm.RIGHT);
      }
   }

   @Inject(
      method = {"positionRightArm"},
      at = {@At("TAIL")}
   )
   private void elysium$poseRightArmForCheirosiphon(T livingEntity, CallbackInfo ci) {
      if (livingEntity.getMainHandStack().isOf(Elysium.CHEIROSIPHON)) {
         CheirosiphonItemRenderer.animateHold(this.rightArm, this.leftArm, this.head, livingEntity.getMainArm() == Arm.RIGHT);
      } else if (livingEntity.getOffHandStack().isOf(Elysium.CHEIROSIPHON)) {
         CheirosiphonItemRenderer.animateOffhandHold(this.rightArm, this.leftArm, this.head, livingEntity.getMainArm() == Arm.RIGHT);
      }
   }
}
