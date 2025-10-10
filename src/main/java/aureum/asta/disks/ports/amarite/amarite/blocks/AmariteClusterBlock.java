package aureum.asta.disks.ports.amarite.amarite.blocks;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect.Animator;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.World;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.world.WorldView;
import net.minecraft.util.math.random.Random;
import net.minecraft.state.StateManager.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;

public class AmariteClusterBlock extends AmariteBlock implements Waterloggable {
   public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
   protected final VoxelShape northShape;
   protected final VoxelShape southShape;
   protected final VoxelShape eastShape;
   protected final VoxelShape westShape;
   protected final VoxelShape upShape;
   protected final VoxelShape downShape;

   public AmariteClusterBlock(int height, int xzOffset, Settings settings) {
      super(settings);
      this.setDefaultState((BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, Boolean.FALSE)).with(FACING, Direction.UP));
      this.upShape = Block.createCuboidShape((double)xzOffset, 0.0, (double)xzOffset, (double)(16 - xzOffset), (double)height, (double)(16 - xzOffset));
      this.downShape = Block.createCuboidShape((double)xzOffset, (double)(16 - height), (double)xzOffset, (double)(16 - xzOffset), 16.0, (double)(16 - xzOffset));
      this.northShape = Block.createCuboidShape(
         (double)xzOffset, (double)xzOffset, (double)(16 - height), (double)(16 - xzOffset), (double)(16 - xzOffset), 16.0
      );
      this.southShape = Block.createCuboidShape((double)xzOffset, (double)xzOffset, 0.0, (double)(16 - xzOffset), (double)(16 - xzOffset), (double)height);
      this.eastShape = Block.createCuboidShape(0.0, (double)xzOffset, (double)xzOffset, (double)height, (double)(16 - xzOffset), (double)(16 - xzOffset));
      this.westShape = Block.createCuboidShape((double)(16 - height), (double)xzOffset, (double)xzOffset, 16.0, (double)(16 - xzOffset), (double)(16 - xzOffset));
   }

   public void randomDisplayTick(BlockState state, World world, BlockPos pos, @NotNull Random random) {
      if (random.nextInt(5) == 0) {
         float r = 0.6745098F;
         float g = 0.3882353F;
         float b = 0.8784314F;
         Vec3d point = Vec3d.ofCenter(pos);
         ParticleBuilders.create(AmariteParticles.AMARITE)
            .overrideAnimator(Animator.WITH_AGE)
            .setLifetime(8)
            .setAlpha(0.6F, 0.0F)
            .setAlphaEasing(Easing.CUBIC_IN)
            .setColorCoefficient(0.8F)
            .setColorEasing(Easing.CIRC_OUT)
            .setSpinEasing(Easing.SINE_IN)
            .setColor(r, g, b, 1.0F)
            .setScale(0.24F, 0.12F)
            .setSpinOffset((float)world.getRandom().nextInt(360))
            .setSpin(world.getRandom().nextBoolean() ? 0.5F : -0.5F)
            .randomMotion(0.01F)
            .randomOffset(0.5)
            .spawn(world, point.x, point.y, point.z);
      }

      super.randomDisplayTick(state, world, pos, random);
   }

   public PistonBehavior getPistonBehavior(BlockState state) {
      return PistonBehavior.DESTROY;
   }

   public VoxelShape getOutlineShape(@NotNull BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      Direction direction = (Direction)state.get(FACING);

      return switch (direction) {
         case NORTH -> this.northShape;
         case SOUTH -> this.southShape;
         case EAST -> this.eastShape;
         case WEST -> this.westShape;
         case DOWN -> this.downShape;
         case UP -> this.upShape;
         default -> throw new IncompatibleClassChangeError();
      };
   }

   public boolean canPlaceAt(@NotNull BlockState state, @NotNull WorldView world, @NotNull BlockPos pos) {
      Direction direction = (Direction)state.get(FACING);
      BlockPos blockPos = pos.offset(direction.getOpposite());
      return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction);
   }

   public BlockState getStateForNeighborUpdate(
      @NotNull BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
   ) {
      if ((Boolean)state.get(WATERLOGGED)) {
         world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
      }

      return direction == ((Direction)state.get(FACING)).getOpposite() && !state.canPlaceAt(world, pos)
         ? Blocks.AIR.getDefaultState()
         : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
   }

   @Nullable
   @Override
   public BlockState getPlacementState(@NotNull ItemPlacementContext ctx) {
      WorldAccess worldAccess = ctx.getWorld();
      BlockPos blockPos = ctx.getBlockPos();
      return (BlockState)((BlockState)this.getDefaultState().with(WATERLOGGED, worldAccess.getFluidState(blockPos).getFluid() == Fluids.WATER))
         .with(FACING, ctx.getSide());
   }

   public FluidState getFluidState(@NotNull BlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
   }

   @Override
   protected void appendProperties(@NotNull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{WATERLOGGED, FACING});
   }

   public static class AmariteBud extends AmariteClusterBlock {
      public AmariteBud(int height, int xzOffset, Settings settings) {
         super(height, xzOffset, settings);
      }

      @Override
      public void randomDisplayTick(BlockState state, World world, BlockPos pos, @NotNull Random random) {
      }
   }
}
