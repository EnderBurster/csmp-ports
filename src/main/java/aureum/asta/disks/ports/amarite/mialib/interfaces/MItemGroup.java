package aureum.asta.disks.ports.amarite.mialib.interfaces;

import java.util.function.Supplier;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public interface MItemGroup {
   default Supplier<ItemStack> mialib$hasConstantIcon() {
      return null;
   }

   default ItemGroup mialib$setConstantIcon(Supplier<ItemStack> constantIcon) {
      return null;
   }
}
