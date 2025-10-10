/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package aureum.asta.disks.mixin.enchantments.thunderstruck;

import aureum.asta.disks.cca.entity.ThunderstruckComponent;
import aureum.asta.disks.init.AstaEntityComponents;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow public abstract void requestTeleport(double destX, double destY, double destZ);

	@ModifyReturnValue(method = "hasNoGravity", at = @At("RETURN"))
	private boolean enchancement$lightningDash(boolean original) {
		ThunderstruckComponent lightningDashComponent = AstaEntityComponents.THUNDERSTRUCK.getNullable(this);
		if (lightningDashComponent != null && lightningDashComponent.isFloating()) {
			return true;
		}
		return original;
	}

	@ModifyExpressionValue(
			method = "writeNbt(Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/nbt/NbtCompound;",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/entity/Entity;hasNoGravity()Z"
			)
	)
	private boolean asta$modifyHasNoGravityInWriteNbt(boolean original) {
		ThunderstruckComponent lightningDashComponent = AstaEntityComponents.THUNDERSTRUCK.getNullable(this);
		if (lightningDashComponent != null && lightningDashComponent.isFloating()) {
			return false;
		}
		return original;
	}
}
