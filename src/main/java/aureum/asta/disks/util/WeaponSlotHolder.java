package aureum.asta.disks.util;

import net.minecraft.item.ItemStack;

public interface WeaponSlotHolder {
   int arsenal$getSlotHolding(ItemStack var1);

   boolean arsenal$tryInsertIntoSlot(int var1, ItemStack var2);
}
