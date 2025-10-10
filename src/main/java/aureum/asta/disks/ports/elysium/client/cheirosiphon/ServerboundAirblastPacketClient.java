package aureum.asta.disks.ports.elysium.client.cheirosiphon;

import aureum.asta.disks.ports.elysium.cheirosiphon.CheirosiphonItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

@Environment(EnvType.CLIENT)
public class ServerboundAirblastPacketClient {
   public static void sendToServer() {
      ClientPlayNetworking.send(CheirosiphonItem.ServerboundAirblastPacket.PACKET_ID, PacketByteBufs.create());
   }
}
