package aureum.asta.disks.ports.charter.common.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface AelpecyemIsCool extends Inventory {
   static AelpecyemIsCool of(DefaultedList<ItemStack> items) {
      return () -> items;
   }

   static AelpecyemIsCool ofSize(int size) {
      return of(DefaultedList.ofSize(size, ItemStack.EMPTY));
   }

   DefaultedList<ItemStack> getItems();

   default int size() {
      return this.getItems().size();
   }

   default boolean isEmpty() {
      for (int i = 0; i < this.size(); i++) {
         ItemStack stack = this.getStack(i);
         if (!stack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   default ItemStack getStack(int slot) {
      return (ItemStack)this.getItems().get(slot);
   }

   default void markDirty() {
   }

   default ItemStack removeStack(int slot, int amount) {
      ItemStack result = Inventories.splitStack(this.getItems(), slot, amount);
      if (!result.isEmpty()) {
         this.markDirty();
      }

      return result;
   }

   default ItemStack removeStack(int slot) {
      return Inventories.removeStack(this.getItems(), slot);
   }

   default void setStack(int slot, ItemStack stack) {
      if (this.isValid(slot, stack)) {
         this.getItems().set(slot, stack.split(this.getMaxCountPerStack()));
      }

      this.markDirty();
   }

   default void clear() {
      for (int i = 0; i < this.size(); i++) {
         this.setStack(i, ItemStack.EMPTY);
      }

      this.markDirty();
   }

   default boolean canPlayerUse(PlayerEntity player) {
      return true;
   }

   default ItemStack getStack(Item item) {
      for (int i = 0; i < this.size(); i++) {
         if (item.equals(this.getStack(i).getItem())) {
            return this.getStack(i);
         }
      }

      return ItemStack.EMPTY;
   }
}
