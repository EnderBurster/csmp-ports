package aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.emitter;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.base.ScreenParticle;
import net.minecraft.item.ItemStack;

public class ParticleEmitter {
   public final EmitterSupplier supplier;

   public ParticleEmitter(EmitterSupplier supplier) {
      this.supplier = supplier;
   }

   public void tick(ItemStack stack, float x, float y, ScreenParticle.RenderOrder renderOrder) {
      this.supplier.tick(stack, x, y, renderOrder);
   }

   public interface EmitterSupplier {
      void tick(ItemStack var1, float var2, float var3, ScreenParticle.RenderOrder var4);
   }
}
