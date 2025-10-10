package aureum.asta.disks.ports.amarite.amarite.items;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketItem;
import dev.emi.trinkets.api.TrinketsApi;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Formatting;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.World;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.item.Equipment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEnchantments;
import aureum.asta.disks.ports.amarite.mialib.util.MMath;

public class MaskItem extends TrinketItem implements Equipment {
   public final Identifier maskTexture;
   private final int color;

   public MaskItem(Identifier texture, int color, FabricItemSettings settings) {
      super(settings);
      this.maskTexture = texture;
      this.color = color;
   }

   public static ItemStack getWornMask(LivingEntity livingEntity) {
      Optional<TrinketComponent> component = TrinketsApi.getTrinketComponent(livingEntity);
      if (component.isPresent()) {
         for (Pair<SlotReference, ItemStack> pair : component.get().getAllEquipped()) {
            if (((ItemStack)pair.getRight()).getItem() instanceof MaskItem) {
               return (ItemStack)pair.getRight();
            }
         }
      }

      return ItemStack.EMPTY;
   }

   public static int getOffset(@NotNull ItemStack stack) {
      return stack.getNbt() == null ? 0 : stack.getNbt().getInt("offset");
   }

   public static void incrementOffset(@NotNull ItemStack stack) {
      NbtCompound compound = stack.getOrCreateNbt();
      compound.putInt("offset", MMath.clampLoop(compound.getInt("offset") + 1, -2, 3));
   }

   public int mialib$getNameColor(ItemStack stack) {
      return this.color;
   }

   public void appendTooltip(@NotNull ItemStack stack, @Nullable World world, @NotNull List<Text> tooltip, TooltipContext context) {
      if (EnchantmentHelper.getLevel(AmariteEnchantments.ANONYMITY, stack) > 0) {
         tooltip.add(Text.translatable("item.aureum-asta-disks.mask.anonymity").mialib$withColor(5789879));
      }

      if (EnchantmentHelper.getLevel(AmariteEnchantments.CONCEALMENT, stack) > 0) {
         tooltip.add(Text.translatable("item.aureum-asta-disks.mask.concealment").mialib$withColor(13992792));
      }

      tooltip.add(
         Text.translatable("item.aureum-asta-disks.mask.offset_1", new Object[]{Text.translatable("key.mouse.right").mialib$withColor(7699656)})
            .formatted(Formatting.GRAY)
      );
      if (stack.getNbt() != null) {
         tooltip.add(
            Text.translatable("item.aureum-asta-disks.mask.offset_2", new Object[]{stack.getOrCreateNbt().getInt("offset")}).formatted(Formatting.GRAY)
         );
      }

      super.appendTooltip(stack, world, tooltip, context);
   }

   public boolean hasGlint(ItemStack stack) {
      return false;
   }

   public boolean isEnchantable(ItemStack stack) {
      return true;
   }

   public int getEnchantability() {
      return 36;
   }

   @Override
   public EquipmentSlot getSlotType() {
      return EquipmentSlot.HEAD;
   }
}
