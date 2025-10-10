package aureum.asta.disks.mixin.client;

import aureum.asta.disks.cca.BackWeaponComponent;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({CapeFeatureRenderer.class})
public class CapeFeatureRendererMixin {
    @ModifyConstant(
            method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V"},
            constant = {@Constant(
                    floatValue = 32.0F,
                    ordinal = 0
            )}
    )
    public float arsenal$clampCapeRotationR(float constant, @Local(argsOnly = true) AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return !BackWeaponComponent.getBackWeapon(abstractClientPlayerEntity).isEmpty()
                && !BackWeaponComponent.isHoldingBackWeapon(abstractClientPlayerEntity)
                ? 0.0F
                : constant;
    }

    @ModifyConstant(
            method = {"render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V"},
            constant = {@Constant(
                    floatValue = 150.0F,
                    ordinal = 0
            )}
    )
    public float arsenal$clampCapeRotationQ(float constant, @Local(argsOnly = true) AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return !BackWeaponComponent.getBackWeapon(abstractClientPlayerEntity).isEmpty()
                && !BackWeaponComponent.isHoldingBackWeapon(abstractClientPlayerEntity)
                ? 0.0F
                : constant;
    }
}
