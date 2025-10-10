package aureum.asta.disks.mixin.client;

import aureum.asta.disks.ports.amarite.mialib.interfaces.MParticle;
import net.minecraft.client.particle.Particle;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Particle.class)
public class ParticleMixin implements MParticle {
}
