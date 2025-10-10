package aureum.asta.disks.packets;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.init.AstaEntityComponents;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class UseThunderstruckPacket {
    public static final Identifier ID = AureumAstaDisks.id("use_lightning_dash");

    private final int entityId;
    private final Vec3d lungeVelocity;
    private final int floatTicks;

    public UseThunderstruckPacket(int entityId, Vec3d lungeVelocity, int floatTicks) {
        this.entityId = entityId;
        this.lungeVelocity = lungeVelocity;
        this.floatTicks = floatTicks;
    }

    public static void send(ServerPlayerEntity player, Entity entity, Vec3d lungeVelocity, int floatTicks) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeVarInt(entity.getId());
        buf.writeDouble(lungeVelocity.x);
        buf.writeDouble(lungeVelocity.y);
        buf.writeDouble(lungeVelocity.z);
        buf.writeVarInt(floatTicks);

        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void receive() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            Vec3d velocity = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
            int floatTicks = buf.readVarInt();

            client.execute(() -> {
                Entity entity = client.world.getEntityById(entityId);
                if (entity != null) {
                    AstaEntityComponents.THUNDERSTRUCK.maybeGet(entity).ifPresent(lightningDashComponent -> {
                        lightningDashComponent.useCommon(velocity, floatTicks);
                        lightningDashComponent.useClient();
                    });
                }
            });
        });
    }
}
