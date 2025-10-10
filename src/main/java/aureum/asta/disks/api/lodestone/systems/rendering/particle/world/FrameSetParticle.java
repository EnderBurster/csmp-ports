package aureum.asta.disks.api.lodestone.systems.rendering.particle.world;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import java.util.ArrayList;
import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl;
import net.minecraft.client.world.ClientWorld;

public class FrameSetParticle extends GenericParticle {
   public ArrayList<Integer> frameSet = new ArrayList<>();

   public FrameSetParticle(
      ClientWorld world, WorldParticleEffect data, FabricSpriteProviderImpl spriteSet, double x, double y, double z, double xd, double yd, double zd
   ) {
      super(world, data, spriteSet, x, y, z, xd, yd, zd);
   }

   @Override
   public void tick() {
      if (this.age < this.frameSet.size()) {
         this.setSprite(this.frameSet.get(this.age));
      }

      super.tick();
   }

   @Override
   public SimpleParticleEffect.Animator getAnimator() {
      return SimpleParticleEffect.Animator.FIRST_INDEX;
   }

   protected void addLoop(int min, int max, int times) {
      for (int i = 0; i < times; i++) {
         this.addFrames(min, max);
      }
   }

   protected void addFrames(int min, int max) {
      for (int i = min; i <= max; i++) {
         this.frameSet.add(i);
      }
   }

   protected void insertFrames(int insertIndex, int min, int max) {
      for (int i = min; i <= max; i++) {
         this.frameSet.add(insertIndex, i);
      }
   }
}
