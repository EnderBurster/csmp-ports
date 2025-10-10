package aureum.asta.disks.api.lodestone.systems.rendering.particle.screen;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.base.ScreenParticle;
import net.minecraft.world.World;

public class ScreenParticleType<T extends ScreenParticleEffect> {
   public Factory<T> factory;

   public interface Factory<T extends ScreenParticleEffect> {
      ScreenParticle createParticle(World var1, T var2, double var3, double var5, double var7, double var9);
   }
}
