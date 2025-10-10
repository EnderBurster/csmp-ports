package aureum.asta.disks.client.particle;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.client.particle.effect.SparkParticleEffect;
import aureum.asta.disks.client.particle.type.SparkParticleType;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface AstaParticles {
    Map<ParticleType<?>, Identifier> PARTICLES = new LinkedHashMap<>();

    SparkParticleType SPARK = create("spark", new SparkParticleType());

    static void initialize()
    {
        PARTICLES.keySet().forEach(particle -> Registry.register(Registries.PARTICLE_TYPE, PARTICLES.get(particle), particle));
    }

    private static <T extends ParticleType<?>> T create(String name, T particle) {
        PARTICLES.put(particle, AureumAstaDisks.id(name));
        return particle;
    }

    static void registerFactories()
    {
        ParticleFactoryRegistry.getInstance().register(SPARK, SparkParticle.Factory::new);
    }
}
