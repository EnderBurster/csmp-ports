package aureum.asta.disks.ports.mace.packet;


import net.minecraft.network.listener.ClientPlayPacketListener;

public abstract class WindChargeExplosionReader {
   public static WindChargeExplosionReader reader;

   public WindChargeExplosionReader() {
   }

   public abstract void onExplosion(WindChargeExplosionS2CPacket var1, ClientPlayPacketListener var2);
}
