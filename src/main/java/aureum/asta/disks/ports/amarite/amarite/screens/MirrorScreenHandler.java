package aureum.asta.disks.ports.amarite.amarite.screens;

import java.util.Optional;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ArrayPropertyDelegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

public class MirrorScreenHandler extends ScreenHandler {
   private final Inventory mirror = new SimpleInventory(1) {
      public boolean isValid(int slot, @NotNull ItemStack stack) {
         return stack.isOf(AmariteItems.AMARITE_MIRROR);
      }
   };
   private final MirrorScreenHandler.MirrorSlot mirrorSlot;
   private final ScreenHandlerContext context;
   private final PropertyDelegate propertyDelegate;

   public MirrorScreenHandler(int syncId, Inventory inventory) {
      this(syncId, inventory, new ArrayPropertyDelegate(3), ScreenHandlerContext.EMPTY);
   }

   public MirrorScreenHandler(int syncId, Inventory inventory, PropertyDelegate propertyDelegate, ScreenHandlerContext context) {
      super(ScreenHandlerType.BEACON, syncId);
      checkDataCount(propertyDelegate, 3);
      this.propertyDelegate = propertyDelegate;
      this.context = context;
      this.mirrorSlot = new MirrorScreenHandler.MirrorSlot(this.mirror, 0, 136, 110);
      this.addSlot(this.mirrorSlot);
      this.addProperties(propertyDelegate);

      for (int k = 0; k < 3; k++) {
         for (int l = 0; l < 9; l++) {
            this.addSlot(new Slot(inventory, l + k * 9 + 9, 36 + l * 18, 137 + k * 18));
         }
      }

      for (int k = 0; k < 9; k++) {
         this.addSlot(new Slot(inventory, k, 36 + k * 18, 195));
      }
   }

   public void onClosed(PlayerEntity player) {
      super.onClosed(player);
      if (!player.world.isClient) {
         ItemStack itemStack = this.mirrorSlot.takeStack(this.mirrorSlot.getMaxItemCount());
         if (!itemStack.isEmpty()) {
            player.dropItem(itemStack, false);
         }
      }
   }

   public boolean canUse(PlayerEntity player) {
      return canUse(this.context, player, Blocks.BEACON);
   }

   public void setProperty(int id, int value) {
      super.setProperty(id, value);
      this.sendContentUpdates();
   }

   public ItemStack quickMove(PlayerEntity player, int index) {
      ItemStack itemStack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasStack()) {
         ItemStack itemStack2 = slot.getStack();
         itemStack = itemStack2.copy();
         if (index == 0) {
            if (!this.insertItem(itemStack2, 1, 37, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickTransfer(itemStack2, itemStack);
         } else if (!this.mirrorSlot.hasStack() && this.mirrorSlot.canInsert(itemStack2) && itemStack2.getCount() == 1) {
            if (!this.insertItem(itemStack2, 0, 1, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 1 && index < 28) {
            if (!this.insertItem(itemStack2, 28, 37, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 28 && index < 37) {
            if (!this.insertItem(itemStack2, 1, 28, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.insertItem(itemStack2, 1, 37, false)) {
            return ItemStack.EMPTY;
         }

         if (itemStack2.isEmpty()) {
            slot.setStackNoCallbacks(ItemStack.EMPTY);
         } else {
            slot.markDirty();
         }

         if (itemStack2.getCount() == itemStack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTakeItem(player, itemStack2);
      }

      return itemStack;
   }

   public int getProperties() {
      return this.propertyDelegate.get(0);
   }

   @Nullable
   public StatusEffect getPrimaryEffect() {
      return StatusEffect.byRawId(this.propertyDelegate.get(1));
   }

   @Nullable
   public StatusEffect getSecondaryEffect() {
      return StatusEffect.byRawId(this.propertyDelegate.get(2));
   }

   public void setEffects(Optional<StatusEffect> primary, Optional<StatusEffect> secondary) {
      if (this.mirrorSlot.hasStack()) {
         this.propertyDelegate.set(1, primary.<Integer>map(StatusEffect::getRawId).orElse(-1));
         this.propertyDelegate.set(2, secondary.<Integer>map(StatusEffect::getRawId).orElse(-1));
         this.mirrorSlot.takeStack(1);
         this.context.run(World::markDirty);
      }
   }

   public boolean hasPayment() {
      return !this.mirror.getStack(0).isEmpty();
   }

   static {
      ServerPlayNetworking.registerGlobalReceiver(Amarite.id("mirror"), (server, player, handler, buf, responseSender) -> {
         StatusEffect primary = buf.readBoolean() ? StatusEffect.byRawId(buf.readInt()) : null;
         StatusEffect secondary = buf.readBoolean() ? StatusEffect.byRawId(buf.readInt()) : null;
         server.execute(() -> {
            ScreenHandler screenHandler = player.currentScreenHandler;
            if (screenHandler instanceof MirrorScreenHandler) {
               ((MirrorScreenHandler)screenHandler).setEffects(Optional.ofNullable(primary), Optional.ofNullable(secondary));
            }
         });
      });
   }

   private static class MirrorSlot extends Slot {
      public MirrorSlot(Inventory inventory, int index, int x, int y) {
         super(inventory, index, x, y);
      }

      public boolean canInsert(@NotNull ItemStack stack) {
         return stack.isOf(AmariteItems.AMARITE_MIRROR);
      }

      public int getMaxItemCount() {
         return 1;
      }
   }
}
