package aureum.asta.disks.packets;

import aureum.asta.disks.AureumAstaDisks;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class ThunderstruckPacket {
    public static final Identifier ID = AureumAstaDisks.id("add_lightning_dash_particles");

    private final int entityId;

    public ThunderstruckPacket(int entityId) {
        this.entityId = entityId;
    }

    public static void send(ServerPlayerEntity player, Entity entity) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(entity.getId());
        ServerPlayNetworking.send(player, ID, buf);
    }

    public static void receive() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
            int entityId = buf.readVarInt();
            client.execute(() -> {
                Entity entity = MinecraftClient.getInstance().world.getEntityById(entityId);
                if (entity != null) {
                    addParticles(entity);
                }
            });
        });
    }

    public static void addParticles(Entity entity) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double y = Math.round(entity.getWorld().raycast(new RaycastContext(entity.getPos(), entity.getPos().add(entity.getRotationVector().multiply(4)), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, entity)).getPos().getY() - 1);

        for (int i = 0; i < 360; i += 15) {
            for (int j = 1; j < 7; j++) {
                double x = entity.getX() + MathHelper.sin((float) Math.toRadians(i)) * j / 2.0;
                double z = entity.getZ() + MathHelper.cos((float) Math.toRadians(i)) * j / 2.0;
                mutable.set(x, y, z);
                BlockState state = entity.getWorld().getBlockState(mutable);
                if (!state.isReplaceable() && entity.getWorld().getBlockState(mutable.move(Direction.UP)).isReplaceable()) {
                    BlockStateParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
                    for (int k = 0; k < 8; k++) {
                        entity.getWorld().addParticle(particle, x, mutable.getY() + 0.5, z, 0, 0, 0);
                    }
                }
            }
        }
    }
}
