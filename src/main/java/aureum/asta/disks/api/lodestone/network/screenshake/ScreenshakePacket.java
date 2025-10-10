package aureum.asta.disks.api.lodestone.network.screenshake;

import aureum.asta.disks.api.lodestone.handlers.ScreenshakeHandler;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.screenshake.ScreenshakeInstance;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;


public class ScreenshakePacket implements Packet<ClientPlayPacketListener> {
   public static final Identifier ID = new Identifier("aureum-asta-disks", "screenshake");
   public final int duration;
   public float intensity1;
   public float intensity2;
   public float intensity3;
   public Easing intensityCurveStartEasing = Easing.LINEAR;
   public Easing intensityCurveEndEasing = Easing.LINEAR;

   public ScreenshakePacket(int duration) {
      this.duration = duration;
   }

   public ScreenshakePacket(PacketByteBuf buf) {
      this.duration = buf.readInt();
      this.intensity1 = buf.readFloat();
      this.intensity2 = buf.readFloat();
      this.intensity3 = buf.readFloat();
      this.setEasing(Easing.valueOf(buf.readString()), Easing.valueOf(buf.readString()));
   }

   public ScreenshakePacket setIntensity(float intensity) {
      return this.setIntensity(intensity, intensity);
   }

   public ScreenshakePacket setIntensity(float intensity1, float intensity2) {
      return this.setIntensity(intensity1, intensity2, intensity2);
   }

   public ScreenshakePacket setIntensity(float intensity1, float intensity2, float intensity3) {
      this.intensity1 = intensity1;
      this.intensity2 = intensity2;
      this.intensity3 = intensity3;
      return this;
   }

   public ScreenshakePacket setEasing(Easing easing) {
      this.intensityCurveStartEasing = easing;
      this.intensityCurveEndEasing = easing;
      return this;
   }

   public ScreenshakePacket setEasing(Easing intensityCurveStartEasing, Easing intensityCurveEndEasing) {
      this.intensityCurveStartEasing = intensityCurveStartEasing;
      this.intensityCurveEndEasing = intensityCurveEndEasing;
      return this;
   }

   public void write(PacketByteBuf buf) {
      buf.writeInt(this.duration);
      buf.writeFloat(this.intensity1);
      buf.writeFloat(this.intensity2);
      buf.writeFloat(this.intensity3);
      buf.writeString(this.intensityCurveStartEasing.name);
      buf.writeString(this.intensityCurveEndEasing.name);
   }

   public void apply(ClientPlayPacketListener listener) {
      ScreenshakeHandler.addScreenshake(
         new ScreenshakeInstance(this.duration)
            .setIntensity(this.intensity1, this.intensity2, this.intensity3)
            .setEasing(this.intensityCurveStartEasing, this.intensityCurveEndEasing)
      );
   }
}
