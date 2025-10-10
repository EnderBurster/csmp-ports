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

public record ArcParticleOption(double targetX, double targetY, double targetZ, float scale, int entityId) implements ParticleEffect {
   public static final Codec<ArcParticleOption> CODEC = RecordCodecBuilder.create(
      instance -> instance.group(
               Codec.DOUBLE.fieldOf("targetX").forGetter(ArcParticleOption::targetX),
               Codec.DOUBLE.fieldOf("targetY").forGetter(ArcParticleOption::targetY),
               Codec.DOUBLE.fieldOf("targetZ").forGetter(ArcParticleOption::targetZ),
               Codec.FLOAT.fieldOf("scale").forGetter(ArcParticleOption::scale),
               Codec.INT.optionalFieldOf("entity_id", 0).forGetter(ArcParticleOption::entityId)
            )
            .apply(instance, ArcParticleOption::new)
   );
   public static final Factory<ArcParticleOption> DESERIALIZER = new Factory<ArcParticleOption>() {
      public ArcParticleOption fromCommand(ParticleType<ArcParticleOption> particleType, StringReader stringReader) throws CommandSyntaxException {
         stringReader.expect(' ');
         double x = stringReader.readDouble();
         stringReader.expect(' ');
         double y = stringReader.readDouble();
         stringReader.expect(' ');
         double z = stringReader.readDouble();
         stringReader.expect(' ');
         float s = stringReader.readFloat();
         stringReader.expect(' ');
         int i = stringReader.readInt();
         return new ArcParticleOption(x, y, z, s, i);
      }

      @Override
      public ArcParticleOption read(ParticleType<ArcParticleOption> type, StringReader reader) throws CommandSyntaxException {
         double x = reader.readDouble();
         reader.expect(' ');
         double y = reader.readDouble();
         reader.expect(' ');
         double z = reader.readDouble();
         reader.expect(' ');

         // Parsing the float (maybe size or something)
         float someFloat = reader.readFloat();
         reader.expect(' ');

         // Parsing the int (could be color, count, etc.)
         int someInt = reader.readInt();

         return new ArcParticleOption(x, y, z, someFloat, someInt);
      }

      @Override
      public ArcParticleOption read(ParticleType<ArcParticleOption> particleType, PacketByteBuf friendlyByteBuf) {
         return new ArcParticleOption(
            friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble(), friendlyByteBuf.readDouble(), friendlyByteBuf.readFloat(), friendlyByteBuf.readVarInt()
         );
      }
   };

   public ArcParticleOption(double x, double y, double z) {
      this(x, y, z, 1.0F, 0);
   }

   public ParticleType<?> getType() {
      return Elysium.ARC_PARTICLE;
   }

   public void write(PacketByteBuf buf) {
      buf.writeDouble(this.targetX);
      buf.writeDouble(this.targetY);
      buf.writeDouble(this.targetZ);
      buf.writeFloat(this.scale);
      buf.writeVarInt(this.entityId);
   }

   public String asString() {
      return "%s %f %f %f %f".formatted(Registries.PARTICLE_TYPE.getId(this.getType()), this.targetX(), this.targetY(), this.targetZ(), this.scale());
   }
}
