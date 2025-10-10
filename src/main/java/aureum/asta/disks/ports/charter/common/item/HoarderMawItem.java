package aureum.asta.disks.ports.charter.common.item;

import java.util.List;
import java.util.Optional;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class HoarderMawItem extends Item {
   public static final String COUNT = "held_item_count";

   public HoarderMawItem(Settings settings) {
      super(settings);
   }

   public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
      NbtCompound nbt = stack.getOrCreateNbt();
      if (nbt.contains("item_nm")) {
         tooltip.add(
            Text.literal(nbt.getInt("held_item_count") + " ")
               .append(((Item) Registries.ITEM.get(new Identifier(nbt.getString("item_nm"), nbt.getString("item_path")))).getName())
               .formatted(Formatting.GOLD)
         );
      }
   }

   public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
      if (clickType != ClickType.RIGHT) {
         return false;
      } else {
         ItemStack itemStack = slot.getStack();
         if (itemStack.isEmpty()) {
            remove(stack).ifPresent(slot::insertStack);
         } else if (canAdd(itemStack)) {
            boolean b = add(stack, itemStack);
            if (b) {
               itemStack.decrement(itemStack.getCount());
            }
         }

         return true;
      }
   }

   public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
      if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
         if (otherStack.isEmpty()) {
            remove(stack).ifPresent(cursorStackReference::set);
         } else {
            boolean b = add(stack, otherStack);
            if (b) {
               otherStack.decrement(otherStack.getCount());
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static boolean add(ItemStack yea, ItemStack stack) {
      if (!stack.isEmpty() && stack.getItem().canBeNested()) {
         NbtCompound nbtCompound = yea.getOrCreateNbt();
         if (canAdd(stack)) {
            if (!nbtCompound.contains("item_nm")) {
               nbtCompound.putString("item_nm", Registries.ITEM.getId(stack.getItem()).getNamespace());
               nbtCompound.putString("item_path", Registries.ITEM.getId(stack.getItem()).getPath());
               nbtCompound.putInt("held_item_count", stack.getCount());
               return true;
            }

            if (((Item)Registries.ITEM.get(new Identifier(nbtCompound.getString("item_nm"), nbtCompound.getString("item_path")))).equals(stack.getItem())
               && nbtCompound.contains("held_item_count")) {
               int i = nbtCompound.getInt("held_item_count");
               i += stack.getCount();
               nbtCompound.remove("held_item_count");
               nbtCompound.putInt("held_item_count", i);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static Optional<ItemStack> remove(ItemStack yea) {
      NbtCompound nbtCompound = yea.getOrCreateNbt();
      if (nbtCompound.contains("item_nm")) {
         Item item = (Item)Registries.ITEM.get(new Identifier(nbtCompound.getString("item_nm"), nbtCompound.getString("item_path")));
         int i = item.getMaxCount();
         int count = nbtCompound.getInt("held_item_count");
         if (count - i <= 0) {
            i += count - i;
            nbtCompound.remove("item_nm");
            nbtCompound.remove("item_path");
            nbtCompound.remove("held_item_count");
         } else {
            nbtCompound.remove("held_item_count");
            nbtCompound.putInt("held_item_count", count - i);
         }

         return Optional.of(new ItemStack(item, i));
      } else {
         return Optional.empty();
      }
   }

   private static boolean canAdd(ItemStack stack) {
      return !stack.hasNbt() && !stack.hasCustomName() && !stack.hasEnchantments() && !(stack.getItem() instanceof HoarderMawItem) && !stack.getItem().equals(Items.TOTEM_OF_UNDYING);
   }
}
