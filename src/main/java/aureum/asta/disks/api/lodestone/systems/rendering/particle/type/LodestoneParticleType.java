package aureum.asta.disks.api.lodestone.systems.rendering.particle.type;

import com.mojang.serialization.Codec;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.world.GenericParticle;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.world.WorldParticleEffect;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.particle.ParticleType;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;

public class LodestoneParticleType extends ParticleType<WorldParticleEffect> {
   public LodestoneParticleType() {
      super(false, WorldParticleEffect.DESERIALIZER);
   }

   public boolean shouldAlwaysSpawn() {
      return true;
   }

   public Codec<WorldParticleEffect> getCodec() {
      return WorldParticleEffect.codecFor(this);
   }

   public static record Factory(SpriteProvider sprite) implements ParticleFactory<WorldParticleEffect> {
      public Particle createParticle(WorldParticleEffect data, ClientWorld world, double x, double y, double z, double mx, double my, double mz) {
         return new GenericParticle(world, data, (FabricSpriteProviderImpl)this.sprite, x, y, z, mx, my, mz);
      }
   }
}
