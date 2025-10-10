package aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.emitter;

import aureum.asta.disks.api.lodestone.systems.rendering.particle.screen.base.ScreenParticle;
import net.minecraft.item.ItemStack;

public interface ItemParticleEmitter {
   void particleTick(ItemStack var1, float var2, float var3, ScreenParticle.RenderOrder var4);
}
