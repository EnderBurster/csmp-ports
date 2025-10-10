package aureum.asta.disks.packets;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.init.AstaEntityComponents;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MeteorPacket {
    public static final Identifier ID = AureumAstaDisks.id("use_eruption");

    public static void send(ServerPlayerEntity player, Entity entity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(entity.getId());
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void receive() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            client.execute(() -> {
                World world = client.world;
                if (world != null) {
                    Entity entity = world.getEntityById(entityId);
                    if (entity != null) {
                        AstaEntityComponents.METEOR.maybeGet(entity).ifPresent(component -> {
                            component.useCommon();
                            component.useClient();
                        });
                    }
                }
            });
        });
    }
}
