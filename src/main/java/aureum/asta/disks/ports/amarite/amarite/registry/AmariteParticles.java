package aureum.asta.disks.ports.amarite.amarite.registry;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.type.LodestoneParticleType;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.type.LodestoneParticleType.Factory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public interface AmariteParticles {
   LodestoneParticleType AMARITE = AureumAstaDisks.REGISTRY.register("amarite", new LodestoneParticleType());
   LodestoneParticleType ACCUMULATION = AureumAstaDisks.REGISTRY.register("accumulation", new LodestoneParticleType());

   static void init() {
   }

   @Environment(EnvType.CLIENT)
   static void initFactories() {
      ParticleFactoryRegistry.getInstance().register(AMARITE, Factory::new);
      ParticleFactoryRegistry.getInstance().register(ACCUMULATION, Factory::new);
   }
}
