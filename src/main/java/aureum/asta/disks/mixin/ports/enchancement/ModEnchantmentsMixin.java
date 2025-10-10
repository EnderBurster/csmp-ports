package aureum.asta.disks.mixin.ports.enchancement;

import moriyashiine.enchancement.common.enchantment.EmptyEnchantment;
import moriyashiine.enchancement.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ModEnchantments.class)
public class ModEnchantmentsMixin {

    @Redirect(
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args= {
                                    "intValue=2"
                            },
                            ordinal = 5
                    )
            ),
            at = @At(
                    value = "NEW",
                    target = "(ILnet/minecraft/enchantment/Enchantment$Rarity;Lnet/minecraft/enchantment/EnchantmentTarget;[Lnet/minecraft/entity/EquipmentSlot;)Lmoriyashiine/enchancement/common/enchantment/EmptyEnchantment;",
                    ordinal = 0
            ),
            method = "<clinit>")
    private static EmptyEnchantment asta$dashChest(int maxLevel, Enchantment.Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        return new EmptyEnchantment(2, Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[]{EquipmentSlot.CHEST});
    }

    @Redirect(
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args= {
                                    "intValue=2"
                            },
                            ordinal = 3
                    )
            ),
            at = @At(
                    value = "NEW",
                    target = "(ILnet/minecraft/enchantment/Enchantment$Rarity;Lnet/minecraft/enchantment/EnchantmentTarget;[Lnet/minecraft/entity/EquipmentSlot;)Lmoriyashiine/enchancement/common/enchantment/EmptyEnchantment;",
                    ordinal = 0
            ),
            method = "<clinit>")
    private static EmptyEnchantment asta$strafeLegs(int maxLevel, Enchantment.Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        return new EmptyEnchantment(2, Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[]{EquipmentSlot.LEGS});
    }
}
