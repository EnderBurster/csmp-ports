package aureum.asta.disks.ports.charter.common.item;

import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class EffigyItem extends Item {
   public EffigyItem(Settings settings) {
      super(settings);
   }

   public void onCraft(ItemStack stack, World world, PlayerEntity player) {
      gift(stack, player);
   }

   public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
      if (!isViable(stack) && entity instanceof PlayerEntity player) {
         gift(stack, player);
      }
   }

   public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
      ItemStack stack = user.getStackInHand(hand);
      if (!isSigned(stack) && !user.getUuid().equals(getGifterUUID(stack))) {
         sign(stack, user);
         return TypedActionResult.success(user.getStackInHand(hand));
      } else {
         return TypedActionResult.pass(user.getStackInHand(hand));
      }
   }

   public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
      if (isViable(stack)) {
         tooltip.add(Text.literal("Given by " + getGifterName(stack) + (isSigned(stack) ? "," : ".")).formatted(Formatting.GOLD));
      }

      if (isSigned(stack)) {
         tooltip.add(Text.literal("foolishly").formatted(new Formatting[]{Formatting.DARK_GRAY, Formatting.OBFUSCATED}));
         tooltip.add(Text.literal("signed by " + getSignerName(stack) + ".").formatted(Formatting.YELLOW));
         tooltip.add(Text.literal("Let their fate act as warning.").formatted(new Formatting[]{Formatting.DARK_GRAY, Formatting.OBFUSCATED}));
      }
   }

   public static void gift(ItemStack stack, PlayerEntity entity) {
      stack.getOrCreateSubNbt("charter").putUuid("GifterUUID", entity.getUuid());
      stack.getOrCreateSubNbt("charter").putString("GifterName", entity.getDisplayName().getString());
   }

   public static void sign(ItemStack stack, PlayerEntity entity) {
      stack.getOrCreateSubNbt("charter").putUuid("SignerUUID", entity.getUuid());
      stack.getOrCreateSubNbt("charter").putString("SignerName", entity.getDisplayName().getString());
   }

   public static ItemStack copyTo(ItemStack from, ItemStack to) {
      if (isViable(from)) {
         to.getOrCreateSubNbt("charter").putUuid("GifterUUID", from.getOrCreateNbt().getUuid("GifterUUID"));
         to.getOrCreateSubNbt("charter").putString("GifterName", from.getOrCreateNbt().getString("GifterName"));
      }

      return to;
   }

   public static boolean isViable(ItemStack stack) {
      return !stack.hasNbt()
         ? false
         : stack.getOrCreateSubNbt("charter").contains("GifterUUID") && stack.getOrCreateSubNbt("charter").getUuid("GifterUUID") != null;
   }

   public static boolean isSigned(ItemStack stack) {
      return !stack.hasNbt()
         ? false
         : stack.getOrCreateSubNbt("charter").contains("SignerUUID") && stack.getOrCreateSubNbt("charter").getUuid("SignerUUID") != null;
   }

   public static UUID getGifterUUID(ItemStack stack) {
      return isViable(stack) ? stack.getOrCreateSubNbt("charter").getUuid("GifterUUID") : null;
   }

   public static String getGifterName(ItemStack stack) {
      return isViable(stack) ? stack.getOrCreateSubNbt("charter").getString("GifterName") : "";
   }

   public static UUID getSignerUUID(ItemStack stack) {
      return isSigned(stack) ? stack.getOrCreateSubNbt("charter").getUuid("SignerUUID") : null;
   }

   public static String getSignerName(ItemStack stack) {
      return isSigned(stack) ? stack.getOrCreateSubNbt("charter").getString("SignerName") : "";
   }
}
