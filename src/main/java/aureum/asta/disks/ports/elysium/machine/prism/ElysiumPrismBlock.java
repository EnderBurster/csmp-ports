package aureum.asta.disks.ports.elysium.machine.prism;

import aureum.asta.disks.ports.elysium.machine.ElysiumMachineBlock;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ElysiumPrismBlock extends ElysiumMachineBlock implements Stainable {
   private static final int[] SIGNAL_BY_POWER = new int[]{0, 3, 7, 11, 15};

   public ElysiumPrismBlock(Settings properties) {
      super(properties);
   }

   public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
      if (world.getBlockEntity(pos) instanceof ElysiumPrismBlockEntity ePBE && world instanceof ServerWorld) {
         ePBE.resetPower();
      }

      super.onStateReplaced(state, world, pos, newState, moved);
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new ElysiumPrismBlockEntity(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
      return type == ElysiumMachines.ELYSIUM_PRISM_BLOCK_ENTITY
         ? (
            world instanceof ServerWorld
               ? (l, p, s, e) -> ElysiumPrismBlockEntity.tick(l, p, s, (ElysiumPrismBlockEntity)e)
               : (l, p, s, e) -> ElysiumPrismBlockEntity.clientTick(l, p, s, (ElysiumPrismBlockEntity)e)
         )
         : null;
   }

   @Override
   public boolean isReceivingSide(BlockState state, Direction side) {
      return side != state.get(Properties.FACING);
   }

   public boolean hasComparatorOutput(BlockState state) {
      return true;
   }

   public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
      return SIGNAL_BY_POWER[state.get(ElysiumMachines.ELYSIUM_POWER)];
   }

   public static int lightLevel(BlockState state) {
      return (int)((double)((Integer)state.get(ElysiumMachines.ELYSIUM_POWER)).intValue() * 3.75);
   }

   public DyeColor getColor() {
      return DyeColor.WHITE;
   }
}
