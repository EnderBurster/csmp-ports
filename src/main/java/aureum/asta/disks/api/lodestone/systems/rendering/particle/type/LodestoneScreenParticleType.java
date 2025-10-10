package aureum.asta.disks.api.lodestone.systems.rendering.particle.type;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.GenericScreenParticle;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.ScreenParticleEffect;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.ScreenParticleType;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.base.ScreenParticle;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.world.World;
import net.minecraft.client.particle.SpriteProvider;

public class LodestoneScreenParticleType extends ScreenParticleType<ScreenParticleEffect> {
   public static class Factory implements ScreenParticleType.Factory<ScreenParticleEffect> {
      public final SpriteProvider sprite;

      public Factory(SpriteProvider sprite) {
         this.sprite = sprite;
      }

      @Override
      public ScreenParticle createParticle(World clientWorld, ScreenParticleEffect options, double pX, double pY, double pXSpeed, double pYSpeed) {
         return new GenericScreenParticle(clientWorld, options, (FabricSpriteProviderImpl)this.sprite, pX, pY, pXSpeed, pYSpeed);
      }
   }
}
