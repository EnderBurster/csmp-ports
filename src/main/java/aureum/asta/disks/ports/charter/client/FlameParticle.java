package aureum.asta.disks.ports.charter.client;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.world.GenericParticle;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.world.WorldParticleEffect;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.world.ClientWorld;

public class FlameParticle extends GenericParticle {
   public FlameParticle(
      ClientWorld world, WorldParticleEffect data, FabricSpriteProviderImpl spriteSet, double x, double y, double z, double xd, double yd, double zd
   ) {
      super(world, data, spriteSet, x, y, z, xd, yd, zd);
      this.setSprite(this.random.nextInt(3));
   }
}
