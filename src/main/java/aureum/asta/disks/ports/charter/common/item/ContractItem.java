package aureum.asta.disks.ports.charter.common.item;

import java.util.List;
import java.util.UUID;

import aureum.asta.disks.ports.charter.client.util.ContractClient;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item.Settings;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class ContractItem extends Item {
   public ContractItem(Settings settings) {
      super(settings);
   }

   public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
      ItemStack stack = user.getStackInHand(hand);

      if(!user.isSneaking())
      {
         if (world.isClient) {
            ContractClient.openContractScreen(this.openContract(world, user, hand), user);
            return TypedActionResult.success(user.getStackInHand(hand));
         }

         return TypedActionResult.pass(user.getStackInHand(hand));
      }

      if (!isViable(stack) && !user.getUuid().equals(getGifterUUID(stack))) {
         user.setStackInHand(hand, putContract(stack, user));
         return TypedActionResult.success(user.getStackInHand(hand));
      } else {
         return TypedActionResult.pass(user.getStackInHand(hand));
      }
   }

   @Environment(EnvType.CLIENT)
   private ItemStack openContract(World world, PlayerEntity user, Hand hand)
   {
      ItemStack book = new ItemStack(Items.WRITTEN_BOOK);

      book.getOrCreateNbt().putString("title", "Contract");
      book.getOrCreateNbt().putString("author", "Charter");

      Text page1 = Text.literal("Contract Immutable\n\n")
              .append(Text.literal("By signing this document, I willingly place myself under the jurisdiction of the "))
              .append(Text.literal("charter").styled(s -> s.withObfuscated(true)))
              .append(Text.literal(". I acknowledge the gift given in exchange, and appreciate the great power which it entails."));

      Text page2 = Text.literal("As a protective measure, I allow for this document to act as a link to my ")
              .append(Text.literal("essence").styled(s -> s.withItalic(true)))
              .append(Text.literal(", thus ensuring my non-aggression in future matters. Given further debt is accrued, I also allow this document to act as further leverage and work to punish me"));

      Text page3 = Text.literal("for disobeying the will of the First of the Seven. While loose, I nevertheless accept these chains placed upon me, and don them willingly.");

      String jsonPage1 = Text.Serializer.toJson(page1);
      String jsonPage2 = Text.Serializer.toJson(page2);
      String jsonPage3 = Text.Serializer.toJson(page3);

      NbtList pages = new NbtList();
      pages.add(NbtString.of(jsonPage1));
      pages.add(NbtString.of(jsonPage2));
      pages.add(NbtString.of(jsonPage3));
      //pages.add(NbtString.of(String.valueOf(Text.literal("Second page with info."))));
      book.getOrCreateNbt().put("pages", pages);
      WrittenBookItem.resolve(book, null, user);

      return book;
   }

   public static void gift(ItemStack stack, PlayerEntity entity) {
      stack.getOrCreateSubNbt("charter").putUuid("GifterUUID", entity.getUuid());
      stack.getOrCreateSubNbt("charter").putString("GifterName", entity.getDisplayName().getString());
   }

   public void onCraft(ItemStack stack, World world, PlayerEntity player) {
      gift(stack, player);
   }

   public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
      if (!isInitialized(stack) && entity instanceof PlayerEntity player) {
         gift(stack, player);
      }
   }

   public static UUID getGifterUUID(ItemStack stack) {
      return isInitialized(stack) ? stack.getOrCreateSubNbt("charter").getUuid("GifterUUID") : null;
   }

   public static String getGifterName(ItemStack stack) {
      return isInitialized(stack) ? stack.getOrCreateSubNbt("charter").getString("GifterName") : "";
   }

   public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
      if (isViable(stack)) {
         tooltip.add(Text.literal("behold").formatted(new Formatting[]{Formatting.DARK_GRAY, Formatting.OBFUSCATED}));
         tooltip.add(Text.literal(getIndebtedName(stack)).formatted(Formatting.GOLD));
         tooltip.add(Text.literal("a fool").formatted(new Formatting[]{Formatting.DARK_GRAY, Formatting.OBFUSCATED}));
         tooltip.add(Text.literal("Terms Written.").formatted(Formatting.GOLD));
      }
      else {
         tooltip.add(Text.literal("Use while crouching to sign.").formatted(Formatting.GOLD));
         tooltip.add(Text.literal("behold aaaa aaaa aaa aaaa aaa").formatted(new Formatting[]{Formatting.DARK_GRAY, Formatting.OBFUSCATED}));
      }
   }

   public static ItemStack putContract(ItemStack stack, PlayerEntity entity) {
      stack.getOrCreateSubNbt("charter").putUuid("IndebtedUUID", entity.getUuid());
      stack.getOrCreateSubNbt("charter").putString("IndebtedName", entity.getDisplayName().getString());
      ((CharterPlayerComponent)entity.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID = ContractItem.getGifterUUID(stack);
      return stack;
   }

   public static ItemStack copyTo(ItemStack from, ItemStack to) {
      if (isViable(from)) {
         to.getOrCreateSubNbt("charter").putUuid("IndebtedUUID", from.getOrCreateNbt().getUuid("IndebtedUUID"));
         to.getOrCreateSubNbt("charter").putString("IndebtedName", from.getOrCreateNbt().getString("IndebtedName"));
      }

      return to;
   }

   public static boolean isInitialized(ItemStack stack) {
      return !stack.hasNbt()
              ? false
              : stack.getOrCreateSubNbt("charter").contains("GifterUUID") && stack.getOrCreateSubNbt("charter").getUuid("GifterUUID") != null;
   }

   public static boolean isViable(ItemStack stack) {
      return !stack.hasNbt()
         ? false
         : stack.getOrCreateSubNbt("charter").contains("IndebtedUUID") && stack.getOrCreateSubNbt("charter").getUuid("IndebtedUUID") != null;
   }

   public static void removeDebt(ItemStack stack) {
      if (stack.hasNbt()) {
         stack.getOrCreateSubNbt("charter").remove("IndebtedUUID");
         stack.getOrCreateSubNbt("charter").remove("IndebtedName");
      }
   }

   public static UUID getIndebtedUUID(ItemStack stack) {
      return isViable(stack) ? stack.getOrCreateSubNbt("charter").getUuid("IndebtedUUID") : null;
   }

   public static String getIndebtedName(ItemStack stack) {
      return isViable(stack) ? stack.getOrCreateSubNbt("charter").getString("IndebtedName") : "";
   }
}
