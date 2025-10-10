package aureum.asta.disks.mixin;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.init.AstaEnchantments;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.enchantment.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static aureum.asta.disks.util.EnchantingUtil.alterLevel;


@Mixin( value = {EnchantmentHelper.class},
        priority = 1000)
public class EnchantmentHelperMixin {

    @Inject(
            method = {"getAttackDamage"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private static void asta$apex(ItemStack stack, EntityGroup group, CallbackInfoReturnable<Float> cir) {
        if (EnchancementUtil.hasEnchantment(AstaEnchantments.APEX, stack)) {
            cir.setReturnValue(0.5F);
        }

    }

}