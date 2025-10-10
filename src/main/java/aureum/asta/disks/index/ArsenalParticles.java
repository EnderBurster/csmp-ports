package aureum.asta.disks.index;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.particle.BloodBubbleParticle;
import aureum.asta.disks.particle.SweepAttackParticle;
import aureum.asta.disks.client.particle.BloodBubbleSplatterParticle;
import aureum.asta.disks.particle.type.SweepParticleType;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

public interface ArsenalParticles {
    Map<ParticleType<?>, Identifier> PARTICLES = new LinkedHashMap<>();
    SweepParticleType SWEEP_PARTICLE = create("sweep", new SweepParticleType(true));
    SweepParticleType SWEEP_SHADOW_PARTICLE = create("sweep_shadow", new SweepParticleType(true));
    DefaultParticleType BLOOD_BUBBLE = create("blood_bubble", FabricParticleTypes.simple(true));
    DefaultParticleType BLOOD_BUBBLE_SPLATTER = create("blood_bubble_splatter", FabricParticleTypes.simple(true));

    static void initialize()
    {
        AureumAstaDisks.LOGGER.info("Particles Initialize");
        PARTICLES.keySet().forEach(particle -> Registry.register(Registries.PARTICLE_TYPE, PARTICLES.get(particle), particle));
    }

    private static <T extends ParticleType<?>> T create(String name, T particle) {
        PARTICLES.put(particle, AureumAstaDisks.id(name));
        return particle;
    }

    static void registerFactories()
    {
        AureumAstaDisks.LOGGER.info("Factories Registered");
        ParticleFactoryRegistry.getInstance().register(SWEEP_PARTICLE, SweepAttackParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SWEEP_SHADOW_PARTICLE, SweepAttackParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(BLOOD_BUBBLE, BloodBubbleParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(BLOOD_BUBBLE_SPLATTER, BloodBubbleSplatterParticle.Factory::new);
    }
}
