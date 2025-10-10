package aureum.asta.disks.mixin.barrier;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.init.AstaEnchantments;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static aureum.asta.disks.util.EnchantingUtil.alterLevel;


@Mixin( value = {EnchantmentHelper.class},
        priority = 999)
public class EnchantmentHelperMixin {
    @ModifyReturnValue(method = "getLevel", at = @At("RETURN"))
    private static int enchancement$enchantedToolsHaveEfficiency(int original, Enchantment enchantment, ItemStack stack) {
        if (enchantment == Enchantments.EFFICIENCY) {
            if (!stack.hasEnchantments()) {
                return original;
            }
            return alterLevel(stack, enchantment);
        }
        return original;
    }

    @ModifyReturnValue(method = "getLevel", at = @At("RETURN"))
    private static int asta$disableEnchantments(int original, Enchantment enchantment, ItemStack stack) {
        if(stack.asta$getOwnerEntity() != null && stack.asta$getOwnerEntity().world != null)
        {
            LivingEntity entity = stack.asta$getOwnerEntity();
            for(WaterBarrier bar : entity.world.getComponent(AureumAstaDisks.KYRATOS).barriers)
            {
                if(bar.shouldDisableEnchantments(entity.getPos()) && !entity.world.getComponent(AureumAstaDisks.KYRATOS).hasPassItem(entity))
                {
                    return 0;
                }
            }
        }

        return original;
    }

    @WrapOperation(method = "getEquipmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I"))
    private static int asta$disableEquipmentEnchantments(Enchantment enchantment, ItemStack stack, Operation<Integer> original, @Local(argsOnly = true) LivingEntity entity) {
        if(entity != null && entity.world != null)
        {
            for(WaterBarrier bar : entity.world.getComponent(AureumAstaDisks.KYRATOS).barriers)
            {
                if(bar.shouldDisableEnchantments(entity.getPos()) && !entity.world.getComponent(AureumAstaDisks.KYRATOS).hasPassItem(entity))
                {
                    return 0;
                }
            }
        }
        return original.call(enchantment, stack);
    }

}