package aureum.asta.disks.ports.elysium.machine;

import java.util.Arrays;
import java.util.Optional;

import aureum.asta.disks.AureumAstaDisks;
import net.minecraft.block.*;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;

public abstract class ElysiumMachineBlock extends BlockWithEntity {
   protected ElysiumMachineBlock(Settings properties) {
      super(properties);
      this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(Properties.FACING, Direction.NORTH)).with(ElysiumMachines.ELYSIUM_POWER, 0));
   }

   protected void appendProperties(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{Properties.FACING}).add(new Property[]{ElysiumMachines.ELYSIUM_POWER});
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      Direction dir = ctx.getPlayerLookDirection().getOpposite();
      BlockState partialState = (BlockState)this.getDefaultState().with(Properties.FACING, dir);
      return (BlockState)partialState.with(ElysiumMachines.ELYSIUM_POWER, this.getPower(ctx.getWorld(), ctx.getBlockPos(), partialState));
   }

   public BlockRenderType getRenderType(BlockState state) {
      return BlockRenderType.MODEL;
   }

   public BlockState getStateForNeighborUpdate(
      BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
   ) {
      int power = this.getPower(world, pos, state);
      return (BlockState)state.with(ElysiumMachines.ELYSIUM_POWER, power);
   }

   public int getPower(WorldAccess level, BlockPos pos, BlockState state) {
      int beamPower;
      if (level.getBlockEntity(pos) instanceof BeamPowered beamPowered) {
         beamPower = beamPowered.getBeamPower(level, pos);
      } else {
         beamPower = 0;
      }

      return Math.max(
         beamPower,
         Arrays.stream(Direction.values())
            .filter(d -> this.isReceivingSide(state, d))
            .map(pos::offset)
            .<BlockState>map(level::getBlockState)
            .filter(s -> !s.contains(Properties.LIT) || (Boolean)s.get(Properties.LIT))
            .map(AbstractBlockState::getBlock)
            .map(ElysiumMachines.PRISM_POWERS::get)
            .filter(Optional::isPresent)
            .mapToInt(Optional::get)
            .max()
            .orElse(0)
      );
   }

   public boolean isReceivingSide(BlockState state, Direction side) {
      return side == ((Direction)state.get(Properties.FACING)).getOpposite();
   }
}
