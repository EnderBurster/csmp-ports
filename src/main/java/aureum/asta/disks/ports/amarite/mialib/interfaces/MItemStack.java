package aureum.asta.disks.ports.amarite.mialib.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import org.jetbrains.annotations.NotNull;

public interface MItemStack {
   default ItemStack mialib$enchantStack(Enchantment enchantment, int level) {
      return ItemStack.EMPTY;
   }

   default ItemStack mialib$enchantStack(EnchantmentLevelEntry... enchantmentLevelEntry) {
      return ItemStack.EMPTY;
   }

   default ItemStack mialib$enchantBook(Enchantment enchantment, int level) {
      return ItemStack.EMPTY;
   }

   default ItemStack mialib$enchantBook(EnchantmentLevelEntry... enchantmentLevelEntry) {
      return ItemStack.EMPTY;
   }

   default LivingEntity asta$getOwnerEntity() {return null;}

   default void asta$setOwnerEntity(LivingEntity newOwner) {}

   @NotNull
   static ItemStack enchantStack(@NotNull ItemStack stack, Enchantment enchantment, int level) {
      stack.addEnchantment(enchantment, level);
      return stack;
   }

   @NotNull
   static ItemStack enchantStack(@NotNull ItemStack stack, @NotNull EnchantmentLevelEntry... enchantmentLevelEntry) {
      for (EnchantmentLevelEntry entry : enchantmentLevelEntry) {
         stack.addEnchantment(entry.enchantment, entry.level);
      }

      return stack;
   }

   @NotNull
   static ItemStack enchantBook(@NotNull ItemStack stack, Enchantment enchantment, int level) {
      EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, level));
      return stack;
   }

   @NotNull
   static ItemStack enchantBook(@NotNull ItemStack stack, @NotNull EnchantmentLevelEntry... enchantmentLevelEntry) {
      for (EnchantmentLevelEntry entry : enchantmentLevelEntry) {
         EnchantedBookItem.addEnchantment(stack, entry);
      }

      return stack;
   }
}
