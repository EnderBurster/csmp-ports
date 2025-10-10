package aureum.asta.disks.mixin.ports.tweaks;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({TridentItem.class})
public class TridentItemMixin extends Item {
   public TridentItemMixin(Settings settings) {
      super(settings);
   }

   public boolean canRepair(ItemStack stack, ItemStack ingredient) {
      return ingredient.getItem() == Items.PRISMARINE_SHARD || ingredient.getItem() == Items.PRISMARINE_CRYSTALS || super.canRepair(stack, ingredient);
   }
}
