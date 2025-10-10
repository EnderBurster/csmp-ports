package aureum.asta.disks.api.lodestone;

import aureum.asta.disks.api.lodestone.config.ClientConfig;
import aureum.asta.disks.api.lodestone.handlers.RenderHandler;
import aureum.asta.disks.api.lodestone.network.screenshake.PositionedScreenshakePacket;
import aureum.asta.disks.api.lodestone.network.screenshake.ScreenshakePacket;
import aureum.asta.disks.api.lodestone.setup.LodestoneRenderLayers;
import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.ModContainer;

public class LodestoneLibClient implements ClientModInitializer {
   public void onInitializeClient() {
      MidnightConfig.init("aureum-asta-disks", ClientConfig.class);
      LodestoneRenderLayers.yea();
      RenderHandler.init();
      ClientPlayNetworking.registerGlobalReceiver(
         ScreenshakePacket.ID, (client, handler, buf, responseSender) -> new ScreenshakePacket(buf).apply(client.getNetworkHandler())
      );
      ClientPlayNetworking.registerGlobalReceiver(
         PositionedScreenshakePacket.ID, (client, handler, buf, responseSender) -> PositionedScreenshakePacket.fromBuf(buf).apply(client.getNetworkHandler())
      );
   }
}
