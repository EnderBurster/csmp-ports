package aureum.asta.disks.ports.charter.common.item;

import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import aureum.asta.disks.ports.charter.common.component.EyeState;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CharterEyeItem extends Item {
   public CharterEyeItem(Settings settings) {
      super(settings);
   }

   public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
      if (!isViable(user.getStackInHand(hand))) {
         give(user.getStackInHand(hand), user);
      } else if (getOwnerUUID(user.getStackInHand(hand)).equals(((CharterPlayerComponent)user.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID)
         && ((CharterPlayerComponent)user.getComponent(CharterComponents.PLAYER_COMPONENT)).eyes == EyeState.BLINDED) {
         ((CharterPlayerComponent)user.getComponent(CharterComponents.PLAYER_COMPONENT)).eyes = EyeState.RESTORED;
         ((CharterPlayerComponent)user.getComponent(CharterComponents.PLAYER_COMPONENT)).sync();
      }

      return super.use(world, user, hand);
   }

   public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
      if (isViable(stack)) {
         tooltip.add(Text.literal("Blessed by " + getOwnerName(stack) + ".").formatted(Formatting.GOLD));
      }
   }

   public static void give(ItemStack stack, PlayerEntity entity) {
      stack.getOrCreateSubNbt("charter").putUuid("OwnerUUID", entity.getUuid());
      stack.getOrCreateSubNbt("charter").putString("OwnerName", entity.getDisplayName().getString());
   }

   public static boolean isViable(ItemStack stack) {
      return !stack.hasNbt()
         ? false
         : stack.getOrCreateSubNbt("charter").contains("OwnerUUID") && stack.getOrCreateSubNbt("charter").getUuid("OwnerUUID") != null;
   }

   public static UUID getOwnerUUID(ItemStack stack) {
      return isViable(stack) ? stack.getOrCreateSubNbt("charter").getUuid("OwnerUUID") : null;
   }

   public static String getOwnerName(ItemStack stack) {
      return isViable(stack) ? stack.getOrCreateSubNbt("charter").getString("OwnerName") : "";
   }
}
