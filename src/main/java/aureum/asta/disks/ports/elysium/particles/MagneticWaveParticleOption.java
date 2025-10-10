package aureum.asta.disks.ports.elysium.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import aureum.asta.disks.ports.elysium.Elysium;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleEffect.Factory;
import net.minecraft.registry.Registries;

public record MagneticWaveParticleOption(boolean isReversed, float distance, float widthScale) implements ParticleEffect {
   public static final Codec<MagneticWaveParticleOption> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
               Codec.BOOL.fieldOf("reversed").forGetter(MagneticWaveParticleOption::isReversed),
               Codec.FLOAT.fieldOf("distance").forGetter(MagneticWaveParticleOption::distance),
               Codec.FLOAT.fieldOf("width_scale").forGetter(MagneticWaveParticleOption::widthScale)
            )
            .apply(instance, MagneticWaveParticleOption::new)
   );
   public static final Factory<MagneticWaveParticleOption> DESERIALIZER = new Factory<MagneticWaveParticleOption>() {
      public MagneticWaveParticleOption fromCommand(ParticleType<MagneticWaveParticleOption> particleType, StringReader stringReader) throws CommandSyntaxException {
         stringReader.expect(' ');
         boolean b = stringReader.readBoolean();
         stringReader.expect(' ');
         float f1 = stringReader.readFloat();
         stringReader.expect(' ');
         float f2 = stringReader.readFloat();
         return new MagneticWaveParticleOption(b, f1, f2);
      }

      @Override
      public MagneticWaveParticleOption read(ParticleType<MagneticWaveParticleOption> type, StringReader reader) throws CommandSyntaxException {
         // Read boolean as string then parse
         boolean flag = reader.readBoolean();

         reader.expect(' '); // expect space separator

         // Read first float
         float f1 = reader.readFloat();

         reader.expect(' '); // expect space separator

         // Read second float
         float f2 = reader.readFloat();

         return new MagneticWaveParticleOption(flag, f1, f2);
      }

      @Override
      public MagneticWaveParticleOption read(ParticleType<MagneticWaveParticleOption> particleType, PacketByteBuf friendlyByteBuf) {
         return new MagneticWaveParticleOption(friendlyByteBuf.readBoolean(), friendlyByteBuf.readFloat(), friendlyByteBuf.readFloat());
      }
   };

   public ParticleType<?> getType() {
      return Elysium.MAGNETIC_WAVE_PARTICLE;
   }

   public void write(PacketByteBuf buf) {
      buf.writeBoolean(this.isReversed);
      buf.writeFloat(this.distance);
      buf.writeFloat(this.widthScale);
   }

   public String asString() {
      return "%s %b %f %f".formatted(Registries.PARTICLE_TYPE.getId(this.getType()), this.isReversed, this.distance, this.widthScale);
   }
}
