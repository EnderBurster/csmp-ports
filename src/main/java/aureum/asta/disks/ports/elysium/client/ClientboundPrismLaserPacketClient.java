package aureum.asta.disks.ports.elysium.client;

import aureum.asta.disks.ports.elysium.machine.prism.ElysiumPrismBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class ClientboundPrismLaserPacketClient {
   public static void init() {
      ClientPlayNetworking.registerGlobalReceiver(ElysiumPrismBlockEntity.ClientboundPrismLaserPacket.PACKET_ID, (client, handler, buf, responseSender) -> {
         BlockPos pos = buf.readBlockPos();
         BlockPos endPos = (BlockPos)buf.readNullable(PacketByteBuf::readBlockPos);
         client.execute(() -> {
            if (client.world != null) {
               if (!(client.world.getBlockEntity(pos) instanceof ElysiumPrismBlockEntity prism)) {
                  return;
               }

               prism.setLaserEnd(endPos);
            }
         });
      });
   }
}
