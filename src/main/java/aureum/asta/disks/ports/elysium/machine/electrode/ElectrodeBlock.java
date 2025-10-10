package aureum.asta.disks.ports.elysium.machine.electrode;

import aureum.asta.disks.ports.elysium.machine.ElysiumMachineBlock;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElectrodeBlock extends ElysiumMachineBlock {
   public static final IntProperty CHARGES = IntProperty.of("charges", 0, 4);
   public static final BooleanProperty HAS_ROD = BooleanProperty.of("has_rod");

   public ElectrodeBlock(Settings properties) {
      super(properties);
      this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(CHARGES, 0)).with(HAS_ROD, false));
   }

   @Override
   protected void appendProperties(Builder<Block, BlockState> builder) {
      super.appendProperties(builder);
      builder.add(new Property[]{CHARGES}).add(new Property[]{HAS_ROD});
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return ElysiumMachines.ELECTRODE_BE.instantiate(pos, state);
   }

   public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
      if (!state.isOf(newState.getBlock())) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (blockEntity instanceof Inventory) {
            ItemScatterer.spawn(world, pos, (Inventory)blockEntity);
            world.updateComparators(pos, this);
         }

         super.onStateReplaced(state, world, pos, newState, moved);
      }
   }

   @Override
   public BlockState getStateForNeighborUpdate(
      BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
   ) {
      BlockState res = super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
      return direction == state.get(Properties.FACING) ? (BlockState)res.with(HAS_ROD, neighborState.isIn(ElysiumMachines.LIGHTNING_RODS)) : res;
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
      if (random.nextBetweenExclusive(0, 3) < (Integer)state.get(ElysiumMachines.ELYSIUM_POWER)) {
         Vec3i dir = ((Direction)state.get(Properties.FACING)).getVector();
         world.addParticle(
            ParticleTypes.ELECTRIC_SPARK,
            (double)pos.getX() + 0.5,
            (double)pos.getY() + 0.5,
            (double)pos.getZ() + 0.5,
            (double)dir.getX() * 0.2,
            (double)dir.getY() * 0.2,
            (double)dir.getZ() * 0.2
         );
      }
   }

   @NotNull
   public ActionResult onUse(
      @NotNull BlockState state, World world, @NotNull BlockPos pos, @NotNull PlayerEntity player, @NotNull Hand hand, @NotNull BlockHitResult hit
   ) {
      if (hit.getSide() == state.get(Properties.FACING)
         && player.getStackInHand(hand).getItem() instanceof BlockItem blockItem
         && blockItem.getBlock().getDefaultState().isIn(ElysiumMachines.LIGHTNING_RODS)) {
         return ActionResult.PASS;
      } else if (world.isClient) {
         return ActionResult.SUCCESS;
      } else {
         if (world.getBlockEntity(pos) instanceof ElectrodeBlockEntity eBe && eBe.canBeUsedBy(player)) {
            player.openHandledScreen(eBe);
         }

         return ActionResult.CONSUME;
      }
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
      return world instanceof ServerWorld ? checkType(type, ElysiumMachines.ELECTRODE_BE, ElectrodeBlockEntity::tick) : null;
   }
}
