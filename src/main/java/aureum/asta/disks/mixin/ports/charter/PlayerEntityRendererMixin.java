package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.client.render.HornsFeatureRenderer;
import aureum.asta.disks.ports.charter.client.render.OtherArmFeatureRenderer;
import aureum.asta.disks.ports.charter.common.component.CharterArmComponent;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin({PlayerEntityRenderer.class})
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
   @Unique
   private PlayerEntity plr;

   public PlayerEntityRendererMixin(Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
      super(ctx, model, shadowRadius);
   }

   @Inject(
      method = {"renderArm"},
      at = {@At("HEAD")}
   )
   private void charter$catch(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      AbstractClientPlayerEntity player,
      ModelPart arm,
      ModelPart sleeve,
      CallbackInfo ci
   ) {
      this.plr = player;
   }

   @Inject(
      method = {"renderArm"},
      at = {@At("TAIL")}
   )
   private void charter$renderArm(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      AbstractClientPlayerEntity player,
      ModelPart arm,
      ModelPart sleeve,
      CallbackInfo ci
   ) {
      for (FeatureRenderer feature : this.features) {
         if (feature instanceof OtherArmFeatureRenderer) {
            OtherArmFeatureRenderer fr = (OtherArmFeatureRenderer)feature;
            if (player.getMainArm() == Arm.RIGHT
               ? arm.equals(((PlayerEntityModel)this.model).leftArm) || sleeve.equals(((PlayerEntityModel)this.model).leftSleeve)
               : arm.equals(((PlayerEntityModel)this.model).rightArm) || sleeve.equals(((PlayerEntityModel)this.model).rightSleeve)) {
               fr.render(matrices, vertexConsumers, light, player, 0.0F, 0.0F, MinecraftClient.getInstance().getTickDelta(), 0.0F, 0.0F, 0.0F);
            }
         }
      }
   }

   @WrapWithCondition(
      method = {"renderArm"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/ModelPart;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;II)V"
      )}
   )
   private boolean charter$shouldRender(ModelPart part, MatrixStack stack, VertexConsumer vc, int i, int b) {
      return (
               ((CharterArmComponent)this.plr.getComponent(CharterComponents.ARM_COMPONENT)).handicap
                  || ((CharterArmComponent)this.plr.getComponent(CharterComponents.ARM_COMPONENT)).armOwner != null
                  || ((CharterArmComponent)this.plr.getComponent(CharterComponents.ARM_COMPONENT)).arm != null
                     && !((CharterArmComponent)this.plr.getComponent(CharterComponents.ARM_COMPONENT)).arm.isEmpty()
            )
            && this.plr.getMainArm() == Arm.LEFT
         ? part.equals(((PlayerEntityModel)this.model).leftArm) || part.equals(((PlayerEntityModel)this.model).leftSleeve)
         : part.equals(((PlayerEntityModel)this.model).rightArm) || part.equals(((PlayerEntityModel)this.model).rightSleeve);
   }

   @Inject(
      method = {"<init>"},
      at = {@At("TAIL")}
   )
   private void charter$PlayerEntityRenderer(Context ctx, boolean slim, CallbackInfo callbackInfo) {
      this.addFeature(new HornsFeatureRenderer(this, ctx.getModelLoader()));
      this.addFeature(new OtherArmFeatureRenderer(this, ctx, slim));
   }

   @Inject(
      method = {"setModelPose"},
      at = {@At("TAIL")},
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   private void charter$armBeGone(AbstractClientPlayerEntity player, CallbackInfo ci, PlayerEntityModel<AbstractClientPlayerEntity> playerEntityModel) {
      ModelPart arm = player.getMainArm() == Arm.RIGHT ? playerEntityModel.leftArm : playerEntityModel.rightArm;
      ModelPart sleeve = player.getMainArm() == Arm.RIGHT ? playerEntityModel.leftSleeve : playerEntityModel.rightSleeve;
      boolean bl = ((CharterArmComponent)player.getComponent(CharterComponents.ARM_COMPONENT)).handicap
         || ((CharterArmComponent)player.getComponent(CharterComponents.ARM_COMPONENT)).armOwner != null
         || ((CharterArmComponent)player.getComponent(CharterComponents.ARM_COMPONENT)).arm != null
            && !((CharterArmComponent)player.getComponent(CharterComponents.ARM_COMPONENT)).arm.isEmpty();
      arm.visible = !bl;
      if (playerEntityModel.leftSleeve.visible) {
         sleeve.visible = !bl;
      }
   }
}
