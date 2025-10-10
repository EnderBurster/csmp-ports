package aureum.asta.disks.mixin.ports.charter.broken_divinity;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static aureum.asta.disks.util.EnchantingUtil.alterLevel;


@Mixin( value = {EnchantmentHelper.class},
        priority = 900)
public class EnchantmentHelperMixin {

    @ModifyReturnValue(method = "getLevel", at = @At("RETURN"))
    private static int charter$disableEnchantments(int original, Enchantment enchantment, ItemStack stack) {
        if(stack.asta$getOwnerEntity() instanceof PlayerEntity player && player.getComponent(CharterComponents.PLAYER_COMPONENT).divinityFlying)
        {
            return 0;
        }

        return original;
    }

    @WrapOperation(method = "getEquipmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I"))
    private static int charter$disableEquipmentEnchantments(Enchantment enchantment, ItemStack stack, Operation<Integer> original, @Local(argsOnly = true) LivingEntity entity) {
        if(entity instanceof PlayerEntity player && player.getComponent(CharterComponents.PLAYER_COMPONENT).divinityFlying)
        {
            return 0;
        }
        return original.call(enchantment, stack);
    }

}