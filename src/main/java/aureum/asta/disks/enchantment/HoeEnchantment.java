package aureum.asta.disks.enchantment;

import moriyashiine.enchancement.common.enchantment.EmptyEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.HoeItem;
import net.minecraft.item.ItemStack;

public class HoeEnchantment extends EmptyEnchantment {
    public HoeEnchantment(Enchantment.Rarity weight, EnchantmentTarget type, EquipmentSlot... slotTypes) {
        super(weight, type, slotTypes);
    }

    public HoeEnchantment(int maxLevel, Rarity weight, EnchantmentTarget type, EquipmentSlot... slotTypes) {
        super(maxLevel, weight, type, slotTypes);
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof HoeItem;
    }
}
