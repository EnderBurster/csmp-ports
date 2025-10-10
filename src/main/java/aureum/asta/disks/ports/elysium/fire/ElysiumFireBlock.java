package aureum.asta.disks.ports.elysium.fire;

import aureum.asta.disks.ports.elysium.Elysium;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;

public class ElysiumFireBlock extends AbstractFireBlock {
   public ElysiumFireBlock(Settings properties) {
      super(properties, 2.0F);
   }

   public BlockState getStateForNeighborUpdate(
      BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
   ) {
      return this.canPlaceAt(state, world, pos) ? this.getDefaultState() : Blocks.AIR.getDefaultState();
   }

   public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
      return canSurviveOnBlock(world.getBlockState(pos.down()));
   }

   public static boolean canSurviveOnBlock(BlockState blockState) {
      return blockState.isIn(Elysium.ELYSIUM_FIRE_BASE_BLOCKS);
   }

   protected boolean isFlammable(BlockState state) {
      return true;
   }
}
