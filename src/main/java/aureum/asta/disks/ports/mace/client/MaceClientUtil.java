package aureum.asta.disks.ports.mace.client;

import aureum.asta.disks.ports.mace.FaithfulMace;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class MaceClientUtil {
   public MaceClientUtil() {
   }

   public static RenderLayer getBreezeWind(Identifier texture, float x, float y) {
      return RenderLayer.getEntitySolid(texture);
   }

   public static void spawnSmashAttackParticles(ClientWorld world, BlockPos pos, int count) {
      Vec3d vec3d = pos.toCenterPos().add((double)0.0F, (double)0.5F, (double)0.0F);
      BlockStateParticleEffect blockStateParticleEffect = new BlockStateParticleEffect(FaithfulMace.DUST_PILLAR, world.getBlockState(pos));

      for(int i = 0; (float)i < (float)count / 3.0F; ++i) {
         double d = vec3d.x + world.getRandom().nextGaussian() / (double)2.0F;
         double e = vec3d.y;
         double f = vec3d.z + world.getRandom().nextGaussian() / (double)2.0F;
         double g = world.getRandom().nextGaussian() * (double)0.2F;
         double h = world.getRandom().nextGaussian() * (double)0.2F;
         double j = world.getRandom().nextGaussian() * (double)0.2F;
         world.addParticle(blockStateParticleEffect, d, e, f, g, h, j);
      }

      for(int i = 0; (float)i < (float)count / 1.5F; ++i) {
         double d = vec3d.x + (double)3.5F * Math.cos((double)i) + world.getRandom().nextGaussian() / (double)2.0F;
         double e = vec3d.y;
         double f = vec3d.z + (double)3.5F * Math.sin((double)i) + world.getRandom().nextGaussian() / (double)2.0F;
         double g = world.getRandom().nextGaussian() * (double)0.05F;
         double h = world.getRandom().nextGaussian() * (double)0.05F;
         double j = world.getRandom().nextGaussian() * (double)0.05F;
         world.addParticle(blockStateParticleEffect, d, e, f, g, h, j);
      }

   }
}
