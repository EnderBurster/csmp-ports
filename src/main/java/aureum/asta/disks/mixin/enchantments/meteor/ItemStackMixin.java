package aureum.asta.disks.mixin.enchantments.meteor;

import aureum.asta.disks.cca.entity.MeteorComponent;
import aureum.asta.disks.init.AstaEntityComponents;
import aureum.asta.disks.util.EnchantingUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
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
    private LivingEntity owner = null;

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract int getMaxUseTime();

    @Shadow public abstract TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand);

    @Shadow @Nullable public abstract Entity getHolder();

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void enchancement$eruption(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        setUsing(user, false);

        if (hand == Hand.MAIN_HAND && !EnchantingUtil.isSufficientlyHigh(user, 0.25) && canUse(user)) {
            setUsing(user, true);
            owner = user;
            user.setCurrentHand(hand);
            cir.setReturnValue(TypedActionResult.consume((ItemStack)(Object)this));
        }
    }

    @Inject(method = "getMaxUseTime", at = @At("HEAD"), cancellable = true)
    private void enchancement$eruption(CallbackInfoReturnable<Integer> cir) {
        if (isUsing(owner))
        {
            cir.setReturnValue(72000);
        }
    }

    @Inject(method = "usageTick", at = @At("HEAD"))
    private void enchancement$eruptionTick(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        setUsing(user, canUse(user));
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"), cancellable = true)
    private void enchancement$eruption(World world, LivingEntity user, int remainingUseTicks, CallbackInfo ci) {
        if (isUsing(user)) {
            int useTime = getMaxUseTime() - remainingUseTicks;
            if (useTime >= AstaEntityComponents.METEOR.get(user).getChargeTime()) {
                if (user instanceof PlayerEntity player) {
                    player.incrementStat(Stats.USED.getOrCreateStat(getItem()));
                }
                MeteorComponent component = AstaEntityComponents.METEOR.get(user);
                component.useCommon();
                if (world.isClient) {
                    component.useClient();
                } else {
                    component.useServer();
                }
            }
            ci.cancel();
        }
    }

    @Unique
    private boolean canUse(LivingEntity user) {
        return AstaEntityComponents.METEOR.get(user).getChargeTime() != 0 && AstaEntityComponents.METEOR.get(user).hasMeteor();
    }

    @Unique
    private static boolean isUsing(LivingEntity living) {
        return living != null && AstaEntityComponents.METEOR.get(living).isUsing();
    }

    @Unique
    private static void setUsing(LivingEntity living, boolean using) {
        AstaEntityComponents.METEOR.get(living).setUsing(using);
    }
}
