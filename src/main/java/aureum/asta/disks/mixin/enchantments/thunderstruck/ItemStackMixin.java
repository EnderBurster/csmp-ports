/*
 * Copyright (c) MoriyaShiine. All Rights Reserved.
 */
package aureum.asta.disks.mixin.enchantments.thunderstruck;

import aureum.asta.disks.cca.entity.ThunderstruckComponent;
import aureum.asta.disks.init.AstaEntityComponents;
import aureum.asta.disks.util.EnchantingUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Unique
	private LivingEntity thunderstruck$owner = null;

	@Shadow
	public abstract Item getItem();

	@Shadow
	public abstract int getMaxUseTime();

	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void enchancement$lightningDash(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
		ItemStack stack = user.getStackInHand(hand);
		setUsing(user, false);
		if (hand == Hand.MAIN_HAND && !EnchantingUtil.isSufficientlyHigh(user, 0.25) && canUse(user)) {
			setUsing(user, true);
			thunderstruck$owner = user;
			user.setCurrentHand(hand);
			cir.setReturnValue(TypedActionResult.consume((ItemStack)(Object)this));
		}
	}

	@Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
	private void enchancement$lightningDash(CallbackInfoReturnable<Integer> cir) {
		if (isUsing(thunderstruck$owner)) {
			cir.setReturnValue(72000);
		}
	}

	@Inject(method = "usageTick", at = @At("HEAD"))
	private void enchancement$lightningDashTick(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
		setUsing(user, canUse(user));
	}

	@Inject(method = "onStoppedUsing", at = @At("HEAD"), cancellable = true)
	private void enchancement$lightningDash(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
		if (isUsing(user)) {
			ItemStack stack = (ItemStack) (Object) this;
			int useTime = getMaxUseTime() - remainingUseTicks;
			ThunderstruckComponent lightningDashComponent = AstaEntityComponents.THUNDERSTRUCK.get(user);
			if (useTime >= lightningDashComponent.getChargeTime()) {
				if (user instanceof PlayerEntity player) {
					player.incrementStat(Stats.USED.getOrCreateStat(getItem()));
				}
				Vec3d lungeVelocity = user.getRotationVector().multiply(lightningDashComponent.getLungeStrength());
				int floatTicks = lightningDashComponent.getFloatTime();
				lightningDashComponent.useCommon(lungeVelocity, floatTicks);
				if (world.isClient) {
					lightningDashComponent.useClient();
				} else {
					lightningDashComponent.useServer(lungeVelocity, floatTicks);
				}
			}
			ci.cancel();
		}
	}

	@Unique
	private boolean canUse(LivingEntity user) {
		return AstaEntityComponents.THUNDERSTRUCK.get(user).getChargeTime() != 0 && AstaEntityComponents.THUNDERSTRUCK.get(user).hasThunderstruck();
	}

	@Unique
	private static boolean isUsing(LivingEntity living) {
		return living != null && AstaEntityComponents.THUNDERSTRUCK.get(living).isUsing();
	}

	@Unique
	private static void setUsing(LivingEntity living, boolean using) {
		AstaEntityComponents.THUNDERSTRUCK.get(living).setUsing(using);
	}
}
