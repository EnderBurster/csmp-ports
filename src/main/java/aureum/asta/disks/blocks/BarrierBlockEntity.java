package aureum.asta.disks.blocks;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.entity.grimoire.PageEntity;
import aureum.asta.disks.init.AstaBlockEntities;
import aureum.asta.disks.ports.charter.Charter;
import aureum.asta.disks.sound.AstaSounds;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BarrierBlockEntity extends BlockEntity {
    private boolean active = true;

    public int ticks;
    public float nextPageAngle;
    public float pageAngle;
    public float flipRandom;
    public float flipTurn;
    public float bookRotation;
    public float lastBookRotation;
    public float targetBookRotation;
    public float nextPageTurningSpeed;
    public float pageTurningSpeed;

    private static final Random RANDOM = Random.create();

    public BarrierBlockEntity(BlockPos pos, BlockState state) {
        super(AstaBlockEntities.BARRIER_BLOCK, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BarrierBlockEntity blockEntity) {
        WaterBarrier barrier = world.getComponent(AureumAstaDisks.KYRATOS).getBarrier(pos);
        blockEntity.pageTurningSpeed = blockEntity.nextPageTurningSpeed;
        blockEntity.lastBookRotation = blockEntity.bookRotation;
        PlayerEntity playerEntity = getClosestPlayer((double)pos.getX() + (double)0.5F, (double)pos.getY(), (double)pos.getZ() + (double)0.5F, (double)barrier.getSize() + 0.5, world);
        if (playerEntity != null) {
            if(blockEntity.ticks % 10 == 0 && world.getComponent(AureumAstaDisks.KYRATOS).getBarrier(pos).isActive() && !world.getComponent(AureumAstaDisks.KYRATOS).getBarrier(pos).getActivating())
            {
                Vec3d from = pos.toCenterPos();
                from = from.add(0.0f, 2.5f, 0.0f);
                Vec3d to = playerEntity.getEyePos().add(playerEntity.getVelocity());
                Vec3d dir = to.subtract(from);

                double dx = dir.x;
                double dy = dir.y;
                double dz = dir.z;

                double distXZ = Math.sqrt(dx * dx + dz * dz);

                float pitch = (float) -(Math.atan2(dy, distXZ) * (180.0 / Math.PI));

                float yaw = (float) (Math.atan2(-dx, dz) * (180.0 / Math.PI));

                world.playSound(null, pos, AstaSounds.GRIMOIRE_FIRE, SoundCategory.PLAYERS, 0.3f, 1f);
                PageEntity page = getPageEntity(world, yaw, pitch, blockEntity.getPos().toCenterPos().add(0.0f, 2.5f, 0.0f));

                world.spawnEntity(page);
            }

            double d = playerEntity.getX() - ((double)pos.getX() + (double)0.5F);
            double e = playerEntity.getZ() - ((double)pos.getZ() + (double)0.5F);
            blockEntity.targetBookRotation = (float)MathHelper.atan2(e, d);
            blockEntity.nextPageTurningSpeed += 0.1F;
            if (blockEntity.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
                float f = blockEntity.flipRandom;

                do {
                    blockEntity.flipRandom += (float)(RANDOM.nextInt(4) - RANDOM.nextInt(4));
                } while(f == blockEntity.flipRandom);
            }
        } else {
            blockEntity.targetBookRotation += 0.02F;
            blockEntity.nextPageTurningSpeed -= 0.1F;
        }

        while(blockEntity.bookRotation >= (float)Math.PI) {
            blockEntity.bookRotation -= ((float)Math.PI * 2F);
        }

        while(blockEntity.bookRotation < -(float)Math.PI) {
            blockEntity.bookRotation += ((float)Math.PI * 2F);
        }

        while(blockEntity.targetBookRotation >= (float)Math.PI) {
            blockEntity.targetBookRotation -= ((float)Math.PI * 2F);
        }

        while(blockEntity.targetBookRotation < -(float)Math.PI) {
            blockEntity.targetBookRotation += ((float)Math.PI * 2F);
        }

        float g;
        for(g = blockEntity.targetBookRotation - blockEntity.bookRotation; g >= (float)Math.PI; g -= ((float)Math.PI * 2F)) {
        }

        while(g < -(float)Math.PI) {
            g += ((float)Math.PI * 2F);
        }

        blockEntity.bookRotation += g * 0.4F;
        blockEntity.nextPageTurningSpeed = MathHelper.clamp(blockEntity.nextPageTurningSpeed, 0.0F, 1.0F);
        ++blockEntity.ticks;
        blockEntity.pageAngle = blockEntity.nextPageAngle;
        float h = (blockEntity.flipRandom - blockEntity.nextPageAngle) * 0.4F;
        float i = 0.2F;
        h = MathHelper.clamp(h, -0.2F, 0.2F);
        blockEntity.flipTurn += (h - blockEntity.flipTurn) * 0.9F;
        blockEntity.nextPageAngle += blockEntity.flipTurn;
    }

    private static @NotNull PageEntity getPageEntity(World world, float yaw, float pitch, Vec3d pos) {
        PageEntity page = new PageEntity(world, pos);
        page.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;

        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));
        float g = -MathHelper.sin(pitch * ((float)Math.PI / 180F));
        float h = MathHelper.cos(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));
        page.setVelocity((double)f, (double)g, (double)h, 2.0f, 1.0f);

        //page.setVelocity(blockEntity, pitch, yaw, 0.0F, 2.0F, 1.0F);
        page.setDamage(6);
        return page;
    }

    public boolean isActive()
    {
        return this.active;
    }

    public void setActive(boolean activate)
    {
        this.active = activate;
        markDirty();
    }

    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    private static PlayerEntity getClosestPlayer(double x, double y, double z, double maxDistance, World world) {
        double d = (double)-1.0F;
        PlayerEntity playerEntity = null;

        for(PlayerEntity playerEntity2 : world.getPlayers()) {
            if(world.getComponent(AureumAstaDisks.KYRATOS).hasPassItem(playerEntity2) || Charter.bannedUuids.contains(playerEntity2.getUuid())) continue;
            double X = playerEntity2.getX() - x;
            double Y = playerEntity2.getY() - y;
            double Z = playerEntity2.getZ() - z;
            double e = Math.max(Math.max(X*X, Y*Y), Z*Z);
            if ((maxDistance < (double)0.0F || e < maxDistance * maxDistance) && (d == (double)-1.0F || e < d)) {
                d = e;
                playerEntity = playerEntity2;
            }
        }

        return playerEntity;
    }

}
