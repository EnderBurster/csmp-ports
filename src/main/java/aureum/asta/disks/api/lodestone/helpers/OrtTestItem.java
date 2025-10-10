package aureum.asta.disks.api.lodestone.helpers;

import aureum.asta.disks.api.lodestone.network.screenshake.PositionedScreenshakePacket;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;

public class OrtTestItem extends Item {
   public OrtTestItem(Settings settings) {
      super(settings);
   }

   public ActionResult useOnBlock(ItemUsageContext context) {
      if (context.getWorld() instanceof ServerWorld s) {
         PlayerEntity user = context.getPlayer();
         s.getPlayers(
               players -> players.getWorld()
                     .isChunkLoaded((new ChunkPos(user.getBlockPos())).x, (new ChunkPos(user.getBlockPos())).z)
            )
            .forEach(
               players -> {
                  PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                  new PositionedScreenshakePacket(70, Vec3d.ofCenter(context.getBlockPos()), 20.0F, 0.3F, 25.0F, Easing.CIRC_IN)
                     .setIntensity(0.0F, 1.0F, 0.0F)
                     .setEasing(Easing.CIRC_OUT, Easing.CIRC_IN)
                     .write(buf);
                  ServerPlayNetworking.send(players, PositionedScreenshakePacket.ID, buf);
               }
            );
      }

      return super.useOnBlock(context);
   }
}
