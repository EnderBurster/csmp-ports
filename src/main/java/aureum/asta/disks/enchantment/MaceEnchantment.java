package aureum.asta.disks.enchantment;

import aureum.asta.disks.ports.mace.item.MaceItem;
import moriyashiine.enchancement.common.enchantment.EmptyEnchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class MaceEnchantment extends EmptyEnchantment {
    public MaceEnchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot... slotTypes) {
        super(weight, type, slotTypes);
    }

    public MaceEnchantment(int maxLevel, Rarity weight, EnchantmentTarget type, EquipmentSlot... slotTypes) {
        super(maxLevel, weight, type, slotTypes);
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof MaceItem;
    }
}
