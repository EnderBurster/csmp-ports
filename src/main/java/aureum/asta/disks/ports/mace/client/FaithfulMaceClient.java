package aureum.asta.disks.ports.mace.client;

import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.client.model.WindChargeEntityModel;
import aureum.asta.disks.ports.mace.client.model.WindChargeEntityRenderer;
import aureum.asta.disks.ports.mace.client.particle.GustParticle;
import aureum.asta.disks.ports.mace.entity.ModEntities;
import aureum.asta.disks.ports.mace.packet.WindChargeExplosionReader;
import aureum.asta.disks.ports.mace.packet.WindChargeExplosionS2CPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.network.OffThreadException;

@Environment(EnvType.CLIENT)
public class FaithfulMaceClient implements ClientModInitializer {
   public FaithfulMaceClient() {
   }

   public void onInitializeClient() {
      WindChargeExplosionReader.reader = new ClientWindChargeExplosionReader();
      EntityRendererRegistry.register(ModEntities.WIND_CHARGE, WindChargeEntityRenderer::new);
      WindChargeEntityModel.initModel();
      ParticleFactoryRegistry.getInstance().register(FaithfulMace.GUST, GustParticle.Factory::new);
      ClientPlayNetworking.registerGlobalReceiver(FaithfulMace.EXPLOSION_WIND_CHARGE_S2C_PACKET_ID, (client, handler, buf, responseSender) -> {
         try {
            WindChargeExplosionS2CPacket.read(buf).apply(handler);
         } catch (OffThreadException var5) {
         } catch (Exception e) {
            FaithfulMace.MOGGER.error("Exception caught reading custom explosion packet!", e);
         }

      });
   }
}
