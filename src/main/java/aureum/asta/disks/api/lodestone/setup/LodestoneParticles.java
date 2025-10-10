package aureum.asta.disks.api.lodestone.setup;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.LodestoneLib;
import aureum.asta.disks.api.lodestone.helpers.DataHelper;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.type.LodestoneParticleType;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.Identifier;

public class LodestoneParticles {
   public static final LodestoneParticleType WISP_PARTICLE = new LodestoneParticleType();
   public static final LodestoneParticleType SMOKE_PARTICLE = new LodestoneParticleType();
   public static final LodestoneParticleType SPARKLE_PARTICLE = new LodestoneParticleType();
   public static final LodestoneParticleType TWINKLE_PARTICLE = new LodestoneParticleType();
   public static final LodestoneParticleType STAR_PARTICLE = new LodestoneParticleType();

   public static void init() {
      LodestoneLib.LOGGER.info("Lodestone Initialized");
      initParticles(bind(Registries.PARTICLE_TYPE));
   }

   public static void registerFactories() {
      ParticleFactoryRegistry.getInstance().register(WISP_PARTICLE, LodestoneParticleType.Factory::new);
      ParticleFactoryRegistry.getInstance().register(SMOKE_PARTICLE, LodestoneParticleType.Factory::new);
      ParticleFactoryRegistry.getInstance().register(SPARKLE_PARTICLE, LodestoneParticleType.Factory::new);
      ParticleFactoryRegistry.getInstance().register(TWINKLE_PARTICLE, LodestoneParticleType.Factory::new);
      ParticleFactoryRegistry.getInstance().register(STAR_PARTICLE, LodestoneParticleType.Factory::new);
   }

   private static void initParticles(BiConsumer<ParticleType<?>, Identifier> registry) {
      registry.accept(WISP_PARTICLE, DataHelper.prefix("wisp"));
      registry.accept(SMOKE_PARTICLE, DataHelper.prefix("smoke"));
      registry.accept(SPARKLE_PARTICLE, DataHelper.prefix("sparkle"));
      registry.accept(TWINKLE_PARTICLE, DataHelper.prefix("twinkle"));
      registry.accept(STAR_PARTICLE, DataHelper.prefix("star"));
   }

   private static <T> BiConsumer<T, Identifier> bind(Registry<? super T> registry) {
      return (t, id) -> Registry.register(registry, id, t);
   }
}
