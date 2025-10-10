package aureum.asta.disks.ports.elysium.machine.electrode;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElectrodeBlockItem extends BlockItem {
   public ElectrodeBlockItem(Block block, Settings properties) {
      super(block, properties);
   }

   protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
      boolean result = super.postPlacement(pos, world, player, stack, state);
      if (!world.isClient() && player != null && world.getBlockEntity(pos) instanceof ElectrodeBlockEntity be) {
         be.setOwner(player.getUuid());
         return true;
      } else {
         return result;
      }
   }
}
