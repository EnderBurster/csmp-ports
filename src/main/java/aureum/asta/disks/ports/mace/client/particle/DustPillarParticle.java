package aureum.asta.disks.ports.mace.client.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.particle.BlockDustParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class DustPillarParticle {
   public DustPillarParticle() {
   }

   static @Nullable BlockDustParticle create(BlockStateParticleEffect parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      BlockState blockState = parameters.getBlockState();
      return !blockState.isAir() && !blockState.isOf(Blocks.MOVING_PISTON) && blockState.hasBlockBreakParticles() ? new BlockDustParticle(world, x, y, z, velocityX, velocityY, velocityZ, blockState) : null;
   }

   @Environment(EnvType.CLIENT)
   public static class DustPillarFactory implements ParticleFactory<BlockStateParticleEffect> {
      public DustPillarFactory() {
      }

      public @Nullable Particle createParticle(BlockStateParticleEffect blockStateParticleEffect, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
         Particle particle = DustPillarParticle.create(blockStateParticleEffect, clientWorld, d, e, f, g, h, i);
         if (particle != null) {
            particle.setVelocity(clientWorld.random.nextGaussian() / (double)30.0F, h + clientWorld.random.nextGaussian() / (double)2.0F, clientWorld.random.nextGaussian() / (double)30.0F);
            particle.setMaxAge(clientWorld.random.nextInt(20) + 20);
         }

         return particle;
      }
   }
}
