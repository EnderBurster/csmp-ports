package aureum.asta.disks.ports.amarite.amarite.blocks;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.block.Fertilizable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.FlowerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.WorldView;

public class YellowCarnationPlantBlock extends FlowerBlock implements Fertilizable {
   public YellowCarnationPlantBlock(StatusEffect suspiciousStewEffect, int effectDuration, Settings settings) {
      super(suspiciousStewEffect, effectDuration, settings);
   }

   @Override
   public boolean isFertilizable(WorldView world, BlockPos pos, BlockState state, boolean isClient) {
      return true;
   }

   public boolean canGrow(World world, Random random, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
      dropStack(world, pos, new ItemStack(this));
   }
}
