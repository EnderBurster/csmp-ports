package aureum.asta.disks.api.lodestone.systems.rendering.particle.world;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Codec;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import org.joml.Vector3f;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect.Factory;

public class WorldParticleEffect extends SimpleParticleEffect implements ParticleEffect {
   public ParticleType<?> type;
   public Vector3f startingVelocity = new Vector3f(0.0f, 0.0f, 0.0f);
   public Vector3f endingMotion = new Vector3f(0.0f, 0.0f, 0.0f);
   public static final Factory<WorldParticleEffect> DESERIALIZER = new Factory<WorldParticleEffect>() {
      public WorldParticleEffect read(ParticleType<WorldParticleEffect> type, StringReader reader) {
         return new WorldParticleEffect(type);
      }

      public WorldParticleEffect read(ParticleType<WorldParticleEffect> type, PacketByteBuf buf) {
         return new WorldParticleEffect(type);
      }
   };

   public WorldParticleEffect(ParticleType<?> type) {
      this.type = type;
   }

   public static Codec<WorldParticleEffect> codecFor(ParticleType<?> type) {
      return Codec.unit(() -> new WorldParticleEffect(type));
   }

   public ParticleType<?> getType() {
      return this.type;
   }

   public void write(PacketByteBuf buf) {
   }

   public String asString() {
      return "";
   }
}
