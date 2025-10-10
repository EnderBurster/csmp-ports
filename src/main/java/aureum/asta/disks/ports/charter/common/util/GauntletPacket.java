package aureum.asta.disks.ports.charter.common.util;

import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import aureum.asta.disks.ports.charter.common.component.GauntletMode;
import aureum.asta.disks.ports.charter.common.item.GauntletItem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import org.jetbrains.annotations.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class GauntletPacket {
   public static final Identifier ID = new Identifier("charter", "gauntlet");

   public static void send(@Nullable Entity entity) {
      PacketByteBuf buf = PacketByteBufs.create();
      if (entity != null) {
         buf.writeInt(entity.getId());
      }

      ClientPlayNetworking.send(ID, buf);
   }

   public static void handle(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler network, PacketByteBuf buf, PacketSender sender) {
      if (buf.isReadable()) {
         buf.readInt();
      } else {
         byte var10000 = -1;
      }

      server.execute(() -> {
         if (player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof GauntletItem) {
            if (player.isSneaking() && ((CharterPlayerComponent)player.getComponent(CharterComponents.PLAYER_COMPONENT)).mode != GauntletMode.IDLE) {
               ((CharterPlayerComponent)player.getComponent(CharterComponents.PLAYER_COMPONENT)).mode = GauntletMode.IDLE;
               CharterComponents.PLAYER_COMPONENT.sync(player);
            } else if (((GauntletItem)player.getStackInHand(Hand.MAIN_HAND).getItem()).isAdvance) {
               ((CharterPlayerComponent)player.getComponent(CharterComponents.PLAYER_COMPONENT)).mode = GauntletMode.BLADE;
               CharterComponents.PLAYER_COMPONENT.sync(player);
            } else {
               ((CharterPlayerComponent)player.getComponent(CharterComponents.PLAYER_COMPONENT)).mode = GauntletMode.SWEEP;
               CharterComponents.PLAYER_COMPONENT.sync(player);
            }
         }
      });
   }
}
