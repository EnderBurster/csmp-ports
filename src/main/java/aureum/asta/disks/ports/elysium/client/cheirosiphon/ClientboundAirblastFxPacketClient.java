package aureum.asta.disks.ports.elysium.client.cheirosiphon;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.ElysiumSounds;
import aureum.asta.disks.ports.elysium.cheirosiphon.CheirosiphonItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ClientboundAirblastFxPacketClient {
   public static void init() {
      ClientPlayNetworking.registerGlobalReceiver(
         CheirosiphonItem.ClientboundAirblastFxPacket.PACKET_ID,
         (client, handler, buf, responseSender) -> {
            Vec3d direction = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            Vec3d spawnPosition = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            client.execute(
               () -> {
                  if (client.world != null) {
                     client.world
                        .playSound(
                           client.player,
                           spawnPosition.getX(),
                           spawnPosition.getY(),
                           spawnPosition.getZ(),
                           ElysiumSounds.CHEIROSIPHON_BLAST,
                           SoundCategory.NEUTRAL,
                           1.0F,
                           1.0F
                        );

                     for (int i = 0; i < 3; i++) {
                        client.world
                           .addParticle(
                              Elysium.MAGNETIC_PULSE_PARTICLE,
                              spawnPosition.getX(),
                              spawnPosition.getY(),
                              spawnPosition.getZ(),
                              direction.getX() * (double)((float)(i + 1) / 3.0F),
                              direction.getY() * (double)((float)(i + 1) / 3.0F),
                              direction.getZ() * (double)((float)(i + 1) / 3.0F)
                           );
                     }
                  }
               }
            );
         }
      );
   }
}
