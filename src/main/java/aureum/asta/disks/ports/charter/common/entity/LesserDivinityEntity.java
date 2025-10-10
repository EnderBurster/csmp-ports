package aureum.asta.disks.ports.charter.common.entity;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.handlers.ScreenshakeHandler;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.api.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import aureum.asta.disks.ports.charter.common.block.BrushableBlockEntity;
import aureum.asta.disks.ports.charter.common.damage.CharterDamageSources;
import aureum.asta.disks.ports.charter.common.init.CharterBlocks;
import aureum.asta.disks.ports.charter.common.init.CharterEntities;
import aureum.asta.disks.ports.charter.common.init.CharterItems;
import aureum.asta.disks.ports.charter.common.init.CharterParticles;
import aureum.asta.disks.ports.charter.common.interfaces.LockedTransport;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SuspiciousSandBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;

import java.util.*;

public class LesserDivinityEntity extends Entity implements LockedTransport {
    protected boolean lastUse = false;
    protected boolean broken = false;
    protected boolean shardsScattered = false;

    public LesserDivinityEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public LesserDivinityEntity(World world) {
        super(CharterEntities.LESSER_DIVINITY_ENTITY, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public boolean getBroken()
    {
        return this.broken;
    }

    public void setLastUse(boolean newLastUse)
    {
        this.lastUse = newLastUse;
    }

    public boolean isInvulnerable() {
        return true;
    }

    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    protected Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
        return LivingEntity.positionInPortal(super.positionInPortal(portalAxis, portalRect));
    }

    public boolean canHit() {
        return false;
    }

    public void updatePassengerPosition(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            passenger.setPosition(this.getX(), this.getY() + 0.01F, this.getZ());
            passenger.setYaw(MathHelper.clamp(passenger.getYaw(), this.getYaw() - 45.0F, this.getYaw() + 45.0F));
            passenger.setPitch(MathHelper.clamp(passenger.getPitch(), this.getPitch() - 45.0F, this.getPitch() + 45.0F));
            passenger.setHeadYaw(MathHelper.clamp(passenger.getHeadYaw(), this.getYaw() - 45.0F, this.getYaw() + 45.0F));
            if (passenger instanceof LivingEntity l) {
                passenger.setBodyYaw(MathHelper.clamp(l.bodyYaw, this.getYaw() - 10.0F, this.getYaw() + 10.0F));
            }
        }
    }

    public void tick() {
        super.tick();
        if (this.age >= 100 && !this.world.isClient) {
            attemptBan(this.getFirstPassenger(), this.getServer(), -1, "Your existence was repurposed.");

            if(!this.shardsScattered && this.lastUse)
            {
                this.shardsScattered = true;
                scatterShards();
            }

            this.remove(RemovalReason.DISCARDED);
        }

        this.timedEffects();

        /*if(this.age == 20)
        {
            for (int i = 0; i < 10; i++)
            {
                ParticleBuilders.create(CharterParticles.DIVINITY_BEAM)
                        .setLifetime(180)
                        .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                        .setAlphaEasing(Easing.LINEAR)
                        .setColorCoefficient(2.0f)
                        .setColorEasing(Easing.ELASTIC_OUT)
                        .setScaleEasing(Easing.SINE_IN_OUT)
                        .setColor(0.98f, 0.973f, 0.698f)
                        .setAlpha(1.0f)
                        .setScale(6f, 6f, 6f)
                        .spawn(this.world, this.getX(), this.getY() + 1f + (10f * i), this.getZ());
            }
        }*/
    }

    protected void timedEffects()
    {
        if(this.age == 25)
        {
            ScreenshakeHandler.addScreenshake(new PositionedScreenshakeInstance(130, this.getPos(), 5, 1, 40, Easing.QUINTIC_OUT).setIntensity(1f, 0.0f).setEasing(Easing.BOUNCE_IN_OUT));
        }

        if(this.age >= 25 && this.age <= 100)
        {
            Box box = this.getBoundingBox().expand(3f);
            for (Entity target : this.world.getEntitiesByClass(LivingEntity.class, box, e -> true))
            {
                target.damage(target.getDamageSources().create(CharterDamageSources.LESSER_DIVINITY), 200f);
            }
            for (int i = 0; i < 6; i++)
            {
                ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE )
                        .setLifetime(10 + this.random.nextInt(5))
                        .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                        .setAlphaEasing(Easing.LINEAR)
                        .setColorCoefficient(2.0f)
                        .setColorEasing(Easing.ELASTIC_OUT)
                        .setSpinEasing(Easing.SINE_IN)
                        .setScaleEasing(Easing.SINE_IN_OUT)
                        .setColor(0.98f, 0.973f, 0.698f)
                        .setAlpha(0.1f, 0.0f)
                        .setSpinOffset(this.random.nextFloat())
                        .setScale(4f + this.random.nextFloat()*3f, 2f + this.random.nextFloat(), 4f + this.random.nextFloat()*3)
                        .randomOffset(5.0f, 5.0f)
                        .spawn(this.world, this.getX(), this.getY() + 1f, this.getZ());
            }

            if(!this.lastUse) return;

            this.getWorld().createExplosion(this, this.getX() + (random.nextFloat()-0.5f) * 25, this.getY() + (random.nextFloat()-0.5f) * 2f, this.getZ() + (random.nextFloat()-0.5f) * 25, 8.0f, true, World.ExplosionSourceType.MOB);
        }
    }

    public boolean hasNoGravity() {
        return true;
    }

    @Override
    protected void removePassenger(Entity passenger) {
    }

    @Override
    public boolean hasPlayerRider() {
        return false;
    }

    public void attemptBan(Entity entity, MinecraftServer server, int minutes, String reason) {
        if(entity instanceof ServerPlayerEntity player)
        {
            PlayerManager pm = server.getPlayerManager();

            Date expires = minutes <= 0 ? null : new Date(System.currentTimeMillis() + (minutes * 60L * 1000L));

            BannedPlayerEntry banEntry = new BannedPlayerEntry(player.getGameProfile(), new Date(), "Charter", expires, reason);

            pm.getUserBanList().add(banEntry);
            player.networkHandler.disconnect(net.minecraft.text.Text.literal(reason));
        }
    }

    protected void scatterShards() {
        BlockPos center = this.getBlockPos();
        int radiusX = 22;
        int radiusY = 12;
        int radiusZ = 22;
        int maxReplace = 5;

        List<BlockPos> candidates = new ArrayList<>();
        List<BlockPos> backUpCandidates = new ArrayList<>();

        for (BlockPos pos : BlockPos.iterate(
                center.add(-radiusX, -radiusY, -radiusZ),
                center.add(radiusX, radiusY, radiusZ))) {

            double dx = (pos.getX() - center.getX()) / (double) radiusX;
            double dy = (pos.getY() - center.getY()) / (double) radiusY;
            double dz = (pos.getZ() - center.getZ()) / (double) radiusZ;
            if (dx*dx + dy*dy + dz*dz > 1.0) continue;

            BlockState state = world.getBlockState(pos);
            boolean exposed = false;
            for (Direction dir : Direction.values()) {
                if (world.getBlockState(pos.offset(dir)).isAir()) {
                    exposed = true;
                    break;
                }
            }

            if (!exposed || state.isAir()) continue;

            if (state.isOf(Blocks.DIRT) || state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.SAND)) {
                candidates.add(pos.toImmutable());
            } else {
                backUpCandidates.add(pos.toImmutable());
            }
        }

        Random random = new Random();
        Collections.shuffle(candidates, random);
        Collections.shuffle(backUpCandidates, random);

        List<BlockPos> chosen = new ArrayList<>();
        for (BlockPos candidate : candidates) {
            if (chosen.size() >= maxReplace) break;
            if (!isNearExisting(chosen, candidate)) {
                chosen.add(candidate);
            }
        }

        // Back up if there isn't dirt/grass/sand to replace.
        if (chosen.size() < maxReplace) {
            for (BlockPos backup : backUpCandidates) {
                if (chosen.size() >= maxReplace) break;
                if (!isNearExisting(chosen, backup)) {
                    chosen.add(backup);
                }
            }
        }

        for (BlockPos pos : chosen) {
            if(world.getBlockState(pos).isOf(Blocks.SAND)) world.setBlockState(pos, CharterBlocks.SUSPICIOUS_SAND.getDefaultState());
            else world.setBlockState(pos, CharterBlocks.SUSPICIOUS_DIRT.getDefaultState());

            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof BrushableBlockEntity brushable) {
                brushable.setItem(new ItemStack(CharterItems.LESSER_DIVINITY_SHARD));
            }
        }
    }

    private boolean isNearExisting(List<BlockPos> list, BlockPos pos) {
        for (BlockPos other : list) {
            if (other.isWithinDistance(pos, 4)) {
                return true;
            }
        }
        return false;
    }
}
