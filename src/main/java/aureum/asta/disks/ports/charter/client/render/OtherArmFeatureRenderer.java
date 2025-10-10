package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.mixin.ports.charter.PlayerSkinProviderAccessor;
import aureum.asta.disks.ports.charter.common.component.CharterArmComponent;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;

public class OtherArmFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
   private PlayerEntityModel<AbstractClientPlayerEntity> model;
   private PlayerEntityModel<AbstractClientPlayerEntity> slimModel;
   private final Context ctx;

   public OtherArmFeatureRenderer(
      FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, Context ctx, boolean bl
   ) {
      super(context);
      this.model = new PlayerEntityModel(ctx.getPart(EntityModelLayers.PLAYER), false);
      this.slimModel = new PlayerEntityModel(ctx.getPart(EntityModelLayers.PLAYER_SLIM), true);
      this.ctx = ctx;
   }

   public void render(
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      AbstractClientPlayerEntity entity,
      float limbAngle,
      float limbDistance,
      float tickDelta,
      float animationProgress,
      float headYaw,
      float headPitch
   ) {
      if (((CharterArmComponent)entity.getComponent(CharterComponents.ARM_COMPONENT)).armOwner != null) {
         MinecraftClient cli = MinecraftClient.getInstance();
         boolean[] slim = new boolean[]{false};
         Identifier texture;
         if (((CharterArmComponent)entity.getComponent(CharterComponents.ARM_COMPONENT)).armOwner != null) {
            GameProfile profile = ((CharterArmComponent)entity.getComponent(CharterComponents.ARM_COMPONENT)).armOwner;
            if (cli.world.getPlayerByUuid(profile.getId()) instanceof AbstractClientPlayerEntity cliPlayer) {
               texture = cliPlayer.getSkinTexture();
               slim[0] = cliPlayer.getModel().contains("slim");
            } else {
               Map<Type, MinecraftProfileTexture> map = cli.getSkinProvider().getTextures(profile);
               texture = map.containsKey(Type.SKIN)
                  ? ((PlayerSkinProviderAccessor)cli.getSkinProvider()).invokeLoadSkin(map.get(Type.SKIN), Type.SKIN, (type, id, tex) -> {
                     if (type == Type.SKIN) {
                        String str = tex.getMetadata("model");
                        if (str == null) {
                           str = "default";
                        }

                        slim[0] = str.contains("slim");
                     }
                  })
                  : DefaultSkinHelper.getTexture(Uuids.getUuidFromProfile(profile));
            }
         } else {
            texture = DefaultSkinHelper.getTexture();
         }

         PlayerEntityModel model = slim[0] ? this.slimModel : this.model;
         ((PlayerEntityModel)this.getContextModel()).copyBipedStateTo(model);
         model.rightSleeve.copyTransform(((PlayerEntityModel)this.getContextModel()).rightSleeve);
         model.leftSleeve.copyTransform(((PlayerEntityModel)this.getContextModel()).leftSleeve);
         model.setVisible(false);
         boolean bl = entity.getMainArm() == Arm.RIGHT;
         model.rightArm.visible = !bl;
         model.leftArm.visible = bl;
         model.rightSleeve.visible = !bl;
         model.leftSleeve.visible = bl;
         model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(texture)), light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}
