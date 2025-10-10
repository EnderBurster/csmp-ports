package aureum.asta.disks.ports.charter.common.item;

import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import aureum.asta.disks.ports.charter.common.component.GauntletMode;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class GauntletItem extends Item {
   public final boolean isAdvance;

   public GauntletItem(Settings settings, boolean attack) {
      super(settings);
      this.isAdvance = attack;
   }

   public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
      CharterPlayerComponent component = (CharterPlayerComponent)user.getComponent(CharterComponents.PLAYER_COMPONENT);
      if (user.getStackInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND).getItem() instanceof GauntletItem) {
         if (user.isSneaking()) {
            component.mode = GauntletMode.COLLECT;
            component.sync();
         } else {
            component.mode = this.isAdvance ? GauntletMode.WHEEL : GauntletMode.SHIELD;
            component.sync();
         }
      }

      return TypedActionResult.success(user.getStackInHand(hand), world.isClient);
   }
}
