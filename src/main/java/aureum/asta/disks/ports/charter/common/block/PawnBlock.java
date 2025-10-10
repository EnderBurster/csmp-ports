package aureum.asta.disks.ports.charter.common.block;

import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import aureum.asta.disks.ports.charter.common.item.ContractItem;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class PawnBlock extends Block implements BlockEntityProvider, Waterloggable {
   public PawnBlock(Settings settings) {
      super(settings);
      this.setDefaultState(
         (BlockState)((BlockState)((BlockState)this.getDefaultState().with(Properties.LIT, false)).with(Properties.FACING, Direction.NORTH))
            .with(Properties.WATERLOGGED, false)
      );
   }

   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return createCuboidShape(6.0, 0.0, 6.0, 10.0, 14.0, 10.0);
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return new PawnBlockEntity(pos, state);
   }

   public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
      boolean client = world.isClient;
      if (!client) {
         ItemStack stack = player.getStackInHand(hand);
         PawnBlockEntity press = (PawnBlockEntity)world.getBlockEntity(pos);

         assert press != null;

         if (press.getContract() == ItemStack.EMPTY && ContractItem.isViable(stack)) {
            world.setBlockState(pos, (BlockState)state.with(Properties.LIT, true));
            world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            press.setStack(0, stack);
            press.markDirty();
            return ActionResult.CONSUME;
         }

         if (!press.getItems().isEmpty() && stack.isEmpty() && ContractItem.isViable(press.getContract())) {
            assert press.getContract() != null;

            world.spawnEntity(new ItemEntity(world, player.getX(), player.getY() + 0.5, player.getZ(), press.getContract()));
            press.clear();
            press.removeStack(0);
            world.setBlockState(pos, (BlockState)state.with(Properties.LIT, false));
            return ActionResult.SUCCESS;
         }

         PlayerEntity pawn = world.getPlayerByUuid(ContractItem.getIndebtedUUID(press.getContract()));
         if (stack.getItem().equals(Items.NAME_TAG)
            && stack.hasCustomName()
            && pawn != null
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID != null
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID.equals(player.getUuid())) {
            ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).newName = stack.getName().getString();
            stack.decrement(1);
            ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).sync();
         } else if (stack.getItem().equals(Items.AMETHYST_SHARD)
            && pawn != null
            && !((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).mute
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID != null
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID.equals(player.getUuid())) {
            ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).mute = true;
            stack.decrement(1);
            ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).sync();
         } else if (stack.getItem().equals(Items.ECHO_SHARD)
            && pawn != null
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).mute
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID != null
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID.equals(player.getUuid())) {
            ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).mute = false;
            stack.decrement(1);
            ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).sync();
         } else if (stack.getItem().equals(Items.PAPER)
            && stack.hasCustomName()
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).mute
            && pawn instanceof ServerPlayerEntity spawn
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID != null
            && ((CharterPlayerComponent)pawn.getComponent(CharterComponents.PLAYER_COMPONENT)).ownerUUID.equals(player.getUuid())) {
            MinecraftServer server = spawn.server;
            server.sendMessage(Text.literal("<" + pawn.getDisplayName().toString() + ">: " + stack.getName().toString()));
            stack.decrement(1);
         }
      }

      return ActionResult.success(client);
   }

   public BlockState getStateForNeighborUpdate(
      BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
   ) {
      if (state.contains(Properties.WATERLOGGED) && (Boolean)state.get(Properties.WATERLOGGED)) {
         world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
   }

   public boolean tryFillWithFluid(WorldAccess world, BlockPos pos, BlockState state, FluidState fluidState) {
      if (!(Boolean)state.get(Properties.WATERLOGGED) && fluidState.getFluid() == Fluids.WATER) {
         if (!world.isClient()) {
            world.setBlockState(pos, (BlockState)state.with(Properties.WATERLOGGED, true), 3);
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
         }

         return true;
      } else {
         return false;
      }
   }

   public FluidState getFluidState(BlockState state) {
      return state.contains(Properties.WATERLOGGED) && state.get(Properties.WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
      return (tickerWorld, pos, tickerState, blockEntity) -> PawnBlockEntity.tick(tickerWorld, pos, tickerState, (PawnBlockEntity)blockEntity);
   }

   public BlockState getPlacementState(ItemPlacementContext ctx) {
      BlockState state = (BlockState)this.getDefaultState().with(Properties.FACING, ctx.getPlayerLookDirection());
      if (!state.contains(Properties.WATERLOGGED)) {
         return state;
      } else {
         FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
         boolean source = fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8;
         return (BlockState)state.with(Properties.WATERLOGGED, source);
      }
   }

   public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
      PawnBlockEntity press = (PawnBlockEntity)world.getBlockEntity(pos);
      if (press != null && ContractItem.isViable(press.getContract()) && world.getPlayerByUuid(ContractItem.getIndebtedUUID(press.getContract())) != null) {
         Objects.requireNonNull(world.getPlayerByUuid(ContractItem.getIndebtedUUID(press.getContract()))).kill();
      }

      super.onBreak(world, pos, state, player);
   }

   protected void appendProperties(Builder<Block, BlockState> builder) {
      super.appendProperties(builder.add(new Property[]{Properties.LIT, Properties.FACING, Properties.WATERLOGGED}));
   }
}
