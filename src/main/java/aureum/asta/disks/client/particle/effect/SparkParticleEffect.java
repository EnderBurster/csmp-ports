package aureum.asta.disks.client.particle.effect;

import aureum.asta.disks.client.particle.AstaParticles;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.util.math.Vec3d;

import java.util.Locale;

public record SparkParticleEffect(Vec3d destination) implements ParticleEffect {
    public static final Factory<SparkParticleEffect> FACTORY = new Factory<SparkParticleEffect>() {
        @Override
        public SparkParticleEffect read(ParticleType<SparkParticleEffect> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            double x = reader.readDouble();
            reader.expect(' ');
            double y = reader.readDouble();
            reader.expect(' ');
            double z = reader.readDouble();
            return new SparkParticleEffect(new Vec3d(x, y, z));
        }

        @Override
        public SparkParticleEffect read(ParticleType<SparkParticleEffect> type, PacketByteBuf buf) {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            return new SparkParticleEffect(new Vec3d(x, y, z));
        }
    };

    @Override
    public ParticleType<?> getType() {
        return AstaParticles.SPARK;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeDouble(destination.x);
        buf.writeDouble(destination.y);
        buf.writeDouble(destination.z);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%f %f %f", destination.x, destination.y, destination.z);
    }
}
