package aureum.asta.disks.client.render.feature;

import aureum.asta.disks.interfaces.BackslotExtraLarge;
import aureum.asta.disks.item.ScytheItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import aureum.asta.disks.cca.BackWeaponComponent;
import aureum.asta.disks.util.WeaponSlotCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;

public class BackWeaponFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public BackWeaponFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    public void render(
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light,
            AbstractClientPlayerEntity abstractClientPlayerEntity,
            float limbAngle,
            float limbDistance,
            float tickDelta,
            float animationProgress,
            float headYaw,
            float headPitch
    ) {
        if (!BackWeaponComponent.isHoldingBackWeapon(abstractClientPlayerEntity)) {
            ItemStack stack = BackWeaponComponent.getBackWeapon(abstractClientPlayerEntity);

            if (!stack.isEmpty()) {
                matrices.push();
                boolean hasCape = abstractClientPlayerEntity.canRenderCapeTexture()
                        && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE)
                        && abstractClientPlayerEntity.getCapeTexture() != null
                        && !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.ELYTRA);
                boolean hasChestPlate = !abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST).isEmpty();
                matrices.translate(0.0F, 0.0F, 0.05F + (hasCape ? 0.05F : 0.0F) + (hasChestPlate ? 0.05F : 0.0F));

                float s = getS(abstractClientPlayerEntity, tickDelta);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - s / 2.0F));

                if (abstractClientPlayerEntity.isInSneakingPose()) {
                    matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-25.0F));
                }

                Vector3f scale = new Vector3f(1.0F, 1.0F, 1.0F);
                if (stack.getItem() instanceof ShieldItem)
                {
                    matrices.translate(0.0, 0.5, 0.0);
                    scale = new Vector3f(2.0F, 2.0F, 2.0F);
                }
                else if (stack.getItem() == AmariteItems.AMARITE_LONGSWORD)
                {
                    matrices.translate(-0.1, 0.25, -0.15);
                    matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternionf(0.0F));
                    scale = new Vector3f(1.85F, 1.85F, 1.0F);
                }
                else if(stack.getItem() instanceof BackslotExtraLarge)
                {
                    matrices.translate(0.0, 0.2, -0.15);
                    scale = new Vector3f(2F, 2F, 1.5F);;
                }
                else
                {
                    ActionResult result = ((WeaponSlotCallback)WeaponSlotCallback.EVENT.invoker()).interact(abstractClientPlayerEntity, stack);
                    if (result == ActionResult.FAIL) {
                        matrices.translate(0.0, 0.2, -0.15);
                        scale = new Vector3f(1.5F, 1.5F, 1.5F);;
                    } else {
                        matrices.translate(0.0, 0.25, -0.2);
                    }
                }

                matrices.scale(scale.x, scale.y, scale.z);
                MinecraftClient.getInstance()
                        .getItemRenderer()
                        .renderItem(
                                abstractClientPlayerEntity,
                                stack,
                                ModelTransformationMode.FIXED,
                                false,
                                matrices,
                                vertexConsumers,
                                abstractClientPlayerEntity.getWorld(),
                                light,
                                OverlayTexture.DEFAULT_UV,
                                0
                        );
                matrices.pop();
            }
        }
    }

    private static float getS(AbstractClientPlayerEntity abstractClientPlayerEntity, float tickDelta) {
        double d = MathHelper.lerp((double) tickDelta, abstractClientPlayerEntity.prevCapeX, abstractClientPlayerEntity.capeX)
                - MathHelper.lerp((double) tickDelta, abstractClientPlayerEntity.prevX, abstractClientPlayerEntity.getX());
        double m = MathHelper.lerp((double) tickDelta, abstractClientPlayerEntity.prevCapeZ, abstractClientPlayerEntity.capeZ)
                - MathHelper.lerp((double) tickDelta, abstractClientPlayerEntity.prevZ, abstractClientPlayerEntity.getZ());
        float n = MathHelper.lerpAngleDegrees(tickDelta, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
        double o = (double)MathHelper.sin(n * (float) (Math.PI / 180.0));
        double p = (double)(-MathHelper.cos(n * (float) (Math.PI / 180.0)));

        float s = (float)(d * p - m * o) * 100.0F;
        s = MathHelper.clamp(s, -20.0F, 20.0F);
        return s;
    }
}

