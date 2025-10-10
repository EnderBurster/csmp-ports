package aureum.asta.disks.ports.mace.packet;

import aureum.asta.disks.ports.mace.ExplosionUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public record WindChargeExplosionS2CPacket(Vec3d center, Optional<Vec3d> playerKnockback, ParticleEffect explosionParticle, RegistryEntry<SoundEvent> explosionSound) implements Packet<ClientPlayPacketListener> {
   public WindChargeExplosionS2CPacket(Vec3d center, Optional<Vec3d> playerKnockback, ParticleEffect explosionParticle, RegistryEntry<SoundEvent> explosionSound) {
      this.center = center;
      this.playerKnockback = playerKnockback;
      this.explosionParticle = explosionParticle;
      this.explosionSound = explosionSound;
   }

   public static WindChargeExplosionS2CPacket read(PacketByteBuf buf) {
      Vec3d vec3d = ExplosionUtil.readVec3d(buf);
      Optional<Vec3d> optional = buf.readOptional(ExplosionUtil::readVec3d);
      ParticleType<?> particleType = (ParticleType)buf.readRegistryValue(Registries.PARTICLE_TYPE);
      ParticleEffect particleEffect = ExplosionUtil.readParticleParameters(buf, particleType);
      RegistryEntry<SoundEvent> registryEntry = buf.readRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), SoundEvent::fromBuf);
      return new WindChargeExplosionS2CPacket(vec3d, optional, particleEffect, registryEntry);
   }

   public void write(PacketByteBuf buf) {
      ExplosionUtil.writeVec3d(buf, this.center);
      buf.writeOptional(this.playerKnockback, ExplosionUtil::writeVec3d);
      this.explosionParticle.write(buf);
      buf.writeRegistryValue(Registries.PARTICLE_TYPE, this.explosionParticle.getType());
      buf.writeRegistryEntry(Registries.SOUND_EVENT.getIndexedEntries(), this.explosionSound, (packetByteBuf, soundEvent) -> soundEvent.writeBuf(packetByteBuf));
   }

   public void apply(ClientPlayPacketListener listener) {
      WindChargeExplosionReader.reader.onExplosion(this, listener);
   }

   public Vec3d center() {
      return this.center;
   }

   public Optional<Vec3d> playerKnockback() {
      return this.playerKnockback;
   }

   public ParticleEffect explosionParticle() {
      return this.explosionParticle;
   }

   public RegistryEntry<SoundEvent> explosionSound() {
      return this.explosionSound;
   }
}
