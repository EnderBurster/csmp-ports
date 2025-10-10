/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package aureum.asta.disks.mixin.enchantments.meteor.client;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.init.AstaEntityComponents;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

	@Inject(method = "getArmPose", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getUseAction()Lnet/minecraft/util/UseAction;"), cancellable = true)
	private static void enchancement$eruption(AbstractClientPlayerEntity player, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
		if (hand == Hand.MAIN_HAND && AstaEntityComponents.METEOR.get(player).isUsing()) {
			cir.setReturnValue(BipedEntityModel.ArmPose.THROW_SPEAR);
		}
	}
}
