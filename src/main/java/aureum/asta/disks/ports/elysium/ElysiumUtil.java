package aureum.asta.disks.ports.elysium;

import java.util.Optional;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class ElysiumUtil {
   public static Optional<Item> getItemForEntity(Entity entity) {
      if (entity instanceof ItemEntity iE) {
         return Optional.of(iE.getStack().getItem());
      } else {
         return entity instanceof FallingBlockEntity falling
            ? Optional.of(falling.getBlockState().getBlock().asItem())
            : Optional.ofNullable(entity.getPickBlockStack()).map(ItemStack::getItem);
      }
   }

   public static Vec3d getRandomOrthogonal(Direction dir, Random random) {
      Vec3i normal = dir.getVector();
      return new Vec3d(
         normal.getX() == 0 ? random.nextDouble() - 0.5 : 0.0,
         normal.getY() == 0 ? random.nextDouble() - 0.5 : 0.0,
         normal.getZ() == 0 ? random.nextDouble() - 0.5 : 0.0
      );
   }
}
