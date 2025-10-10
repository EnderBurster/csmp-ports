package aureum.asta.disks.api.lodestone.network.screenshake;

import aureum.asta.disks.api.lodestone.handlers.ScreenshakeHandler;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.util.Identifier;

public class PositionedScreenshakePacket extends ScreenshakePacket {
   public static final Identifier ID = new Identifier("aureum-asta-disks", "positionedscreenshake");
   public final Vec3d position;
   public final float falloffDistance;
   public final float minDot;
   public final float maxDistance;
   public final Easing falloffEasing;

   public PositionedScreenshakePacket(int duration, Vec3d position, float falloffDistance, float minDot, float maxDistance, Easing falloffEasing) {
      super(duration);
      this.position = position;
      this.falloffDistance = falloffDistance;
      this.minDot = minDot;
      this.maxDistance = maxDistance;
      this.falloffEasing = falloffEasing;
   }

   public static PositionedScreenshakePacket fromBuf(PacketByteBuf buf) {
      return (PositionedScreenshakePacket)new PositionedScreenshakePacket(
            buf.readInt(),
            new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble()),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat(),
            Easing.valueOf(buf.readString())
         )
         .setIntensity(buf.readFloat(), buf.readFloat(), buf.readFloat())
         .setEasing(Easing.valueOf(buf.readString()), Easing.valueOf(buf.readString()));
   }

   public PositionedScreenshakePacket(int duration, Vec3d position, float falloffDistance, float maxDistance) {
      this(duration, position, falloffDistance, 0.0F, maxDistance, Easing.LINEAR);
   }

   @Override
   public void write(PacketByteBuf buf) {
      buf.writeInt(this.duration);
      buf.writeDouble(this.position.x);
      buf.writeDouble(this.position.y);
      buf.writeDouble(this.position.z);
      buf.writeFloat(this.falloffDistance);
      buf.writeFloat(this.minDot);
      buf.writeFloat(this.maxDistance);
      buf.writeString(this.falloffEasing.name);
      buf.writeFloat(this.intensity1);
      buf.writeFloat(this.intensity2);
      buf.writeFloat(this.intensity3);
      buf.writeString(this.intensityCurveStartEasing.name);
      buf.writeString(this.intensityCurveEndEasing.name);
   }

   @Override
   public void apply(ClientPlayPacketListener listener) {
      ScreenshakeHandler.addScreenshake(
         new PositionedScreenshakeInstance(this.duration, this.position, this.falloffDistance, this.minDot, this.maxDistance, this.falloffEasing)
            .setIntensity(this.intensity1, this.intensity2, this.intensity3)
            .setEasing(this.intensityCurveStartEasing, this.intensityCurveEndEasing)
      );
   }
}
