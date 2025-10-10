package aureum.asta.disks.enchantment;

import aureum.asta.disks.item.AstaItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class SpewingEnchantment extends Enchantment implements UniqueEnchantment {
    public SpewingEnchantment(Enchantment.Rarity weight, EquipmentSlot... slot)
    {
        super(weight, EnchantmentTarget.WEAPON, slot);
    }

    public int getMinPower(int level) {
        return 20;
    }

    public int getMaxPower(int level) {
        return 50;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean isAcceptableItem(ItemStack stack)
    {
        return  stack.isOf(AstaItems.BLOOD_SCYTHE) || stack.isOf(Items.BOOK) || stack.isOf(Items.ENCHANTED_BOOK);
    }
}
