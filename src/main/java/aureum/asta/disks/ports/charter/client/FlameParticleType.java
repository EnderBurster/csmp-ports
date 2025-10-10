package aureum.asta.disks.ports.charter.client;

import com.mojang.serialization.Codec;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.world.WorldParticleEffect;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleType;

public class FlameParticleType extends ParticleType<WorldParticleEffect> {
   public FlameParticleType() {
      super(false, WorldParticleEffect.DESERIALIZER);
   }

   public Codec<WorldParticleEffect> getCodec() {
      return WorldParticleEffect.codecFor(this);
   }

   public static record Factory(SpriteProvider sprite) implements ParticleFactory<WorldParticleEffect> {
      public Particle createParticle(WorldParticleEffect data, ClientWorld world, double x, double y, double z, double mx, double my, double mz) {
         return new FlameParticle(world, data, (FabricSpriteProviderImpl)this.sprite, x, y, z, mx, my, mz);
      }
   }
}
