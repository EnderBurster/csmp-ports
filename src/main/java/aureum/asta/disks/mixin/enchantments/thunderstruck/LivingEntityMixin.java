/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package aureum.asta.disks.mixin.enchantments.thunderstruck;

import aureum.asta.disks.cca.entity.ThunderstruckComponent;
import aureum.asta.disks.init.AstaEntityComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
	@Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
	private void enchancement$lightningDash(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		ThunderstruckComponent lightningDashComponent = AstaEntityComponents.THUNDERSTRUCK.getNullable(this);
		if (lightningDashComponent != null && lightningDashComponent.isSmashing()) {
			cir.setReturnValue(false);
		}
	}
}
