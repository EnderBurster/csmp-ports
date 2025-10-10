package aureum.asta.disks.mixin.ports.amarite.amarite.mixin.client;

import net.minecraft.util.Arm;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.model.ModelPart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

@Mixin({BipedEntityModel.class})
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> {
   @Shadow
   @Final
   public ModelPart body;
   @Shadow
   @Final
   public ModelPart head;

   @Shadow
   protected abstract Arm getPreferredArm(T var1);

   @Shadow
   protected abstract ModelPart getArm(Arm var1);

   @Inject(
      method = {"animateArms"},
      at = {@At("TAIL")}
   )
   protected void amarite$twoHanding(T entity, float animationProgress, CallbackInfo ci) {
      if (!(this.handSwingProgress <= 0.0F) && entity.getMainHandStack().isOf(AmariteItems.AMARITE_LONGSWORD)) {
         Arm arm = this.getPreferredArm(entity).getOpposite();
         ModelPart modelPart = this.getArm(arm);
         double f = 1.0 - Math.pow((double)(1.0F - this.handSwingProgress), 3.0);
         float h = MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -(this.head.pitch - 0.7F) * 0.75F;
         modelPart.pitch = modelPart.pitch - (MathHelper.sin((float)(f * Math.PI)) * 1.2F + h);
         modelPart.yaw = modelPart.yaw + this.body.yaw * 2.0F;
         modelPart.roll = modelPart.roll + MathHelper.sin(this.handSwingProgress * (float) Math.PI) * -0.4F;
      }
   }
}
