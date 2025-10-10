package aureum.asta.disks.mixin.ports.mace.client;

import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.client.particle.DustPillarParticle;
import aureum.asta.disks.ports.mace.client.particle.GustEmitterParticle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin({ParticleManager.class})
public abstract class CustomParticleTypeFactoryMixin {
   public CustomParticleTypeFactoryMixin() {
   }

   @Shadow
   protected abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> var1, ParticleFactory<T> var2);

   @Shadow
   protected abstract <T extends ParticleEffect> void registerFactory(ParticleType<T> var1, ParticleManager.SpriteAwareFactory<T> var2);

   @Inject(
           at = {@At("HEAD")},
           method = {"registerDefaultFactories"}
   )
   private void init(CallbackInfo info) {
      this.registerFactory(FaithfulMace.DUST_PILLAR, new DustPillarParticle.DustPillarFactory());
      this.registerFactory(FaithfulMace.GUST_EMITTER_SMALL, new GustEmitterParticle.Factory((double)1.0F, 3, 2));
      this.registerFactory(FaithfulMace.GUST_EMITTER_LARGE, new GustEmitterParticle.Factory((double)3.0F, 7, 0));
   }
}
