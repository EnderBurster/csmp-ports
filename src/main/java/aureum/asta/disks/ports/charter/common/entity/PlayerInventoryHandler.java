package aureum.asta.disks.ports.charter.common.entity;

import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class PlayerInventoryHandler implements NamedScreenHandlerFactory, Inventory {
   private final PlayerEntity privateInQuestion;

   public PlayerInventoryHandler(PlayerEntity player) {
      this.privateInQuestion = player;
   }

   public Text getDisplayName() {
      return this.privateInQuestion.getDisplayName();
   }

   @Nullable
   public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
      return new PlayerInventoryScreenHandler(ScreenHandlerType.GENERIC_9X5, i, playerInventory, this, 5);
   }

   public int size() {
      return 45;
   }

   public boolean isEmpty() {
      return this.privateInQuestion.getInventory().isEmpty();
   }

   public ItemStack getStack(int slot) {
      return slot > 44 ? ItemStack.EMPTY : this.privateInQuestion.getInventory().getStack(slot);
   }

   public ItemStack removeStack(int slot, int amount) {
      return slot > 44 ? ItemStack.EMPTY : this.privateInQuestion.getInventory().removeStack(slot, amount);
   }

   public ItemStack removeStack(int slot) {
      return slot > 44 ? ItemStack.EMPTY : this.privateInQuestion.getInventory().removeStack(slot);
   }

   public void setStack(int slot, ItemStack stack) {
      if (slot < 45) {
         this.privateInQuestion.getInventory().setStack(slot, stack);
      }
   }

   public void markDirty() {
      this.privateInQuestion.getInventory().markDirty();
   }

   public boolean canPlayerUse(PlayerEntity player) {
      return ((CharterPlayerComponent)this.privateInQuestion.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID != null
         && player.getUuid().equals(((CharterPlayerComponent)this.privateInQuestion.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID);
   }

   public void clear() {
      this.privateInQuestion.getInventory().clear();
   }

   private static class PlayerInventoryScreenHandler extends GenericContainerScreenHandler {
      public PlayerInventoryScreenHandler(ScreenHandlerType<?> screenHandlerType, int i, PlayerInventory playerInventory, Inventory inventory, int j) {
         super(screenHandlerType, i, playerInventory, inventory, j);
      }

      public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
         switch (slot.getIndex()) {
            case 36:
               return MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.FEET;
            case 37:
               return MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.LEGS;
            case 38:
               return MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.CHEST;
            case 39:
               return MobEntity.getPreferredEquipmentSlot(stack) == EquipmentSlot.HEAD;
            default:
               return slot.getIndex() < 41;
         }
      }

      public boolean canInsertIntoSlot(Slot slot) {
         return slot.getIndex() < 41 && super.canInsertIntoSlot(slot);
      }

      protected boolean insertItem(ItemStack stack, int startIndex, int endIndex, boolean fromLast) {
         return super.insertItem(stack, startIndex, endIndex, fromLast);
      }
   }
}
