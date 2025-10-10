package aureum.asta.disks.ports.amarite.amarite.blocks;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.fluid.Fluids;
import net.minecraft.block.AmethystClusterBlock;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.Direction.Axis;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteBlocks;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;

public class BuddingAmariteBlock extends AmariteBlock {
   public BuddingAmariteBlock(Settings settings) {
      super(settings);
   }

   public void randomTick(BlockState state, ServerWorld world, BlockPos pos, @NotNull Random random) {
      if (random.nextInt(6) == 0) {
         for (Direction direction : Direction.values()) {
            if (direction.getAxis() != Axis.Y) {
               BlockPos offsetPos = pos.offset(direction);
               BlockState offsetState = world.getBlockState(offsetPos);
               if (!offsetState.isOf(Blocks.RAW_GOLD_BLOCK)) {
                  revert(world, pos);
                  return;
               }
            }
         }

         if (!world.getBlockState(pos.offset(Direction.DOWN)).isOf(Blocks.MAGMA_BLOCK)) {
            revert(world, pos);
            return;
         }

         BlockPos blockPos = pos.offset(Direction.UP);
         BlockState blockState = world.getBlockState(blockPos);
         Block block = null;
         if (canGrowIn(blockState)) {
            block = AmariteBlocks.FRESH_AMARITE_BUD;
         } else if (blockState.isOf(AmariteBlocks.FRESH_AMARITE_BUD) && blockState.get(FACING) == Direction.UP) {
            block = AmariteBlocks.PARTIAL_AMARITE_BUD;
         } else if (blockState.isOf(AmariteBlocks.PARTIAL_AMARITE_BUD) && blockState.get(FACING) == Direction.UP) {
            block = AmariteBlocks.AMARITE_CLUSTER;
         }

         if (block != null) {
            BlockState newState = (BlockState)((BlockState)block.getDefaultState().with(AmethystClusterBlock.FACING, Direction.UP))
               .with(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getFluid() == Fluids.WATER);
            world.setBlockState(blockPos, newState);

            for (ServerPlayerEntity entity : world.getPlayers(e -> pos.isWithinDistance(e.getPos(), 64.0)))
            {
               ServerPlayNetworking.send(entity, Amarite.id("budgrow"), PacketByteBufs.create().writeBlockPos(pos));
            }
         }
      }
   }

   private static void revert(@NotNull ServerWorld world, BlockPos pos) {
      world.setBlockState(pos, Blocks.BUDDING_AMETHYST.getDefaultState());
      Vec3d particlePos = Vec3d.ofCenter(pos);
      world.spawnParticles(ParticleTypes.SMOKE, particlePos.getX(), particlePos.getY(), particlePos.getZ(), 24, 0.25, 0.25, 0.25, 0.0);
      world.playSound(
         (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), AmariteSoundEvents.AMARITE_FORMS, null, 1.0F, 0.6F, false
      );
   }

   public static boolean canGrowIn(@NotNull BlockState state) {
      return state.isAir()
         || state.isOf(Blocks.SMALL_AMETHYST_BUD)
         || state.isOf(Blocks.MEDIUM_AMETHYST_BUD)
         || state.isOf(Blocks.LARGE_AMETHYST_BUD)
         || state.isOf(Blocks.AMETHYST_CLUSTER);
   }
}
