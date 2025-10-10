package aureum.asta.disks.ports.charter.common.item;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.charter.common.component.CharterArmComponent;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import java.util.List;
import org.jetbrains.annotations.Nullable;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class HandItem extends Item {
   public HandItem(Settings settings) {
      super(settings);
   }

   public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
      if (stack.getOrCreateNbt().contains("name")) {
         String name = stack.getOrCreateNbt().getString("name");
         tooltip.add(Text.literal(name + "'s Arm").formatted(Formatting.GOLD));
      }
   }

   public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
      if (!world.isClient && user.isSneaking()) {
         if (!((CharterArmComponent)user.getComponent(CharterComponents.ARM_COMPONENT)).handicap && ((CharterArmComponent)user.getComponent(CharterComponents.ARM_COMPONENT)).armOwner == null) {
            ItemStack stack = user.getStackInHand(hand);
            ((CharterArmComponent)user.getComponent(CharterComponents.ARM_COMPONENT)).handicap = true;
            ((CharterArmComponent)user.getComponent(CharterComponents.ARM_COMPONENT)).armOwner = null;
            ((CharterArmComponent)user.getComponent(CharterComponents.ARM_COMPONENT)).sync();
         } else {
            ItemStack stack = user.getStackInHand(hand);
            if (stack.getOrCreateNbt().contains("owner") && NbtHelper.toGameProfile(stack.getOrCreateNbt().getCompound("owner")) != null) {
               ((CharterArmComponent)user.getComponent(CharterComponents.ARM_COMPONENT)).armOwner = NbtHelper.toGameProfile(
                  stack.getOrCreateNbt().getCompound("owner")
               );
               ((CharterArmComponent)user.getComponent(CharterComponents.ARM_COMPONENT)).handicap = false;
               ((CharterArmComponent)user.getComponent(CharterComponents.ARM_COMPONENT)).sync();
               stack.decrement(1);
               return TypedActionResult.success(stack, world.isClient);
            }
         }
      }

      return super.use(world, user, hand);
   }
}
