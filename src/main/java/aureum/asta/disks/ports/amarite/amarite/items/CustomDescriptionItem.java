package aureum.asta.disks.ports.amarite.amarite.items;

import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomDescriptionItem extends Item {
   private final int color;
   private final Text desk;

   public CustomDescriptionItem(int color, Text desk, Settings settings) {
      super(settings);
      this.color = color;
      this.desk = desk;
   }

   public int mialib$getNameColor(ItemStack stack) {
      return this.color;
   }

   public void appendTooltip(ItemStack stack, @Nullable World world, @NotNull List<Text> tooltip, TooltipContext context) {
      tooltip.add(this.desk);
   }
}
