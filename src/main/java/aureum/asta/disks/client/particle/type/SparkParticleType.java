package aureum.asta.disks.client.particle.type;

import aureum.asta.disks.client.particle.effect.SparkParticleEffect;
import com.mojang.serialization.Codec;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;

public class SparkParticleType extends ParticleType<SparkParticleEffect> {
    public SparkParticleType() {
        super(false, SparkParticleEffect.FACTORY);
    }

    @Override
    public Codec<SparkParticleEffect> getCodec() {
        // You can’t really use Codec properly in 1.19.4, so just stub this out
        return Codec.unit(new SparkParticleEffect(new Vec3d(0, 0, 0)));
    }
}
