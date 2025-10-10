package aureum.asta.disks.ports.charter.common.init;

import aureum.asta.disks.ports.charter.Charter;
import aureum.asta.disks.ports.charter.client.FlameParticleType;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.type.LodestoneParticleType;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.type.LodestoneParticleType.Factory;
import java.util.function.BiConsumer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CharterParticles {
   public static LodestoneParticleType COLORED_SMOKE = new LodestoneParticleType();
   public static LodestoneParticleType DIVINITY_SMOKE = new LodestoneParticleType();
   public static LodestoneParticleType DIVINITY_BEAM = new LodestoneParticleType();
   public static LodestoneParticleType EPITAPH_PARTICLE = new LodestoneParticleType();
   public static LodestoneParticleType EPITAPH_SHOCKWAVE = new LodestoneParticleType();
   public static LodestoneParticleType CIRCLE = new LodestoneParticleType();
   public static LodestoneParticleType DIAMOND = new LodestoneParticleType();
   public static LodestoneParticleType SQUARE = new LodestoneParticleType();
   public static FlameParticleType FLAME = new FlameParticleType();

   public static void init() {
      initParticles(bind(Registries.PARTICLE_TYPE));
   }

   public static void registerFactories() {
      ParticleFactoryRegistry.getInstance().register(COLORED_SMOKE, Factory::new);
      ParticleFactoryRegistry.getInstance().register(DIVINITY_SMOKE, Factory::new);
      ParticleFactoryRegistry.getInstance().register(DIVINITY_BEAM, Factory::new);
      ParticleFactoryRegistry.getInstance().register(EPITAPH_PARTICLE, Factory::new);
      ParticleFactoryRegistry.getInstance().register(EPITAPH_SHOCKWAVE, Factory::new);
      ParticleFactoryRegistry.getInstance().register(CIRCLE, Factory::new);
      ParticleFactoryRegistry.getInstance().register(FLAME, FlameParticleType.Factory::new);
      ParticleFactoryRegistry.getInstance().register(DIAMOND, Factory::new);
      ParticleFactoryRegistry.getInstance().register(SQUARE, Factory::new);
   }

   private static void initParticles(BiConsumer<ParticleType<?>, Identifier> registry) {
      registry.accept(COLORED_SMOKE, Charter.id("smoke"));
      registry.accept(DIVINITY_SMOKE, Charter.id("divinity_smoke"));
      registry.accept(DIVINITY_BEAM, Charter.id("divinity_beam"));
      registry.accept(EPITAPH_PARTICLE, Charter.id("epitaph_particle"));
      registry.accept(EPITAPH_SHOCKWAVE, Charter.id("epitaph_shockwave"));
      registry.accept(CIRCLE, Charter.id("circle"));
      registry.accept(FLAME, Charter.id("flame"));
      registry.accept(DIAMOND, Charter.id("diamond"));
      registry.accept(SQUARE, Charter.id("square"));
   }

   private static <T> BiConsumer<T, Identifier> bind(Registry<T> registry) {
      return (t, id) -> Registry.register(registry, id, t);
   }
}
