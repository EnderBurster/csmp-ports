package aureum.asta.disks.entity.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.init.AstaEntities;
import aureum.asta.disks.ports.charter.Charter;
import aureum.asta.disks.ports.mason.entity.TameableHostileEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class VortexEntity extends Entity implements Ownable {
    public VortexEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public VortexEntity(World world) {
        super(AstaEntities.GRIMOIRE_VORTEX, world);
    }

    private static final TrackedData<Integer> LIFESPAN = DataTracker.registerData(VortexEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(VortexEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    public static final TrackedData<Byte> Id = DataTracker.registerData(VortexEntity.class, TrackedDataHandlerRegistry.BYTE);

    @Override
    public void tick() {
        super.tick();

        int lifetime = this.getLifetime();

        if (lifetime <= 0)
        {
            this.discard();
        }

        lifetime--;
        this.setLifetime(lifetime);

        if (!this.world.isClient) {
            pullInNearbyEntities();
            return;
        }

        spawnVortexParticles();
    }

    private void pullInNearbyEntities() {
        double radius = 5.0; // vortex range
        List<LivingEntity> targets = this.world.getEntitiesByClass(
                LivingEntity.class,
                this.getBoundingBox().expand(radius),
                Objects::nonNull
        );

        for (LivingEntity target : targets) {
            if (target.equals(this.getOwner())
                    || (target instanceof TameableEntity tamed && tamed.getOwner() != null && tamed.getOwner().equals(this.getOwner()))
                    || (target instanceof ArmorStandEntity)
                    || (target instanceof TameableHostileEntity tameableHostile && tameableHostile.isOwner(this.getOwner()))
                    || (target instanceof PlayerEntity player && (Charter.bannedUuids.contains(player.getUuid()) || player.isCreative())))
            {
               continue;
            }

            Vec3d pull = this.getPos().subtract(target.getPos());
            double distance = pull.length();

            if (distance > 0.1) {

                double strength = Math.max(Math.min(0.5, (radius - distance) / radius), 0.1);
                Vec3d pullNormalized = pull.normalize().multiply(strength * 0.5);

                target.addVelocity(pullNormalized.x, pullNormalized.y * 0.1, pullNormalized.z);
                target.velocityModified = true;
            }
        }
    }

    private void spawnParticles()
    {
        int count = 30;

        for (int i = 0; i < count; i++) {
            ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE)
                    .setLifetime(12)
                    .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                    .setAlphaEasing(Easing.BOUNCE_IN_OUT)
                    .setColorCoefficient(2.0f)
                    .setColorEasing(Easing.ELASTIC_OUT)
                    .setSpinEasing(Easing.SINE_IN)
                    .setScaleEasing(Easing.SINE_IN_OUT)
                    .setColor(0f, 0.098f, 1f, 0f, 0.078f, 0.788f, 0.8f)
                    .setAlpha(1.0f, 0.5f)
                    .setScale(0.35F + this.random.nextFloat()*0.3f, 0.1F + this.random.nextFloat()*0.1f)
                    .setSpin(this.random.nextBoolean() ? 0.1F : -0.1F)
                    .randomMotion(0.04, 0.01)
                    .randomOffset(2.0F, 0.5F)
                    .setGravity(2f)
                    .spawn(this.world, this.getX(), this.getY() + 2.5, this.getZ());
        }
    }

    private void spawnVortexParticles() {
        double radius = 2.0;
        int rings = 6;
        int baseParticles = 5;
        double heightStep = 0.3;

        for (int ring = 0; ring < rings; ring++) {
            double currentHeight = ring * heightStep;
            double currentRadius = radius * (ring / (double) rings);
            int particlesThisRing = Math.max(4, (int)(baseParticles * (currentRadius / radius)));

            for (int i = 0; i < particlesThisRing; i++) {
                double angle = (this.age * 0.2) + (ring * 0.5);

                double offsetX = Math.cos(angle) * currentRadius;
                double offsetZ = Math.sin(angle) * currentRadius;

                double px = this.getX() + offsetX;
                double py = this.getY() + currentHeight;
                double pz = this.getZ() + offsetZ;

                ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE)
                        .setLifetime((int) Math.max(Math.ceil(40 * ((float)ring / (float) rings)), 1))
                        .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                        .setAlphaEasing(Easing.BOUNCE_IN_OUT)
                        .setColorCoefficient(2.0f)
                        .setColorEasing(Easing.ELASTIC_OUT)
                        .setSpinEasing(Easing.SINE_IN)
                        .setScaleEasing(Easing.SINE_IN_OUT)
                        .setColor(0f, 0.098f, 1f, 0f, 0.078f, 0.788f, 0.8f)
                        .setAlpha(1.0f, 0.5f)
                        .setScale(0.1F + this.random.nextFloat()*0.05f, 0.01f)
                        .setSpin(this.random.nextBoolean() ? 0.1F : -0.1F)
                        .randomMotion(0.04, 0.01)
                        .randomOffset(0.1f, 0.1f)
                        .spawn(this.world, px, py, pz);

            }
        }
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(OWNER_UUID, Optional.of(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2")));
        this.dataTracker.startTracking(LIFESPAN, 200);
        this.dataTracker.startTracking(Id, (byte) 0);
    }

    public int getLifetime()
    {
        return this.dataTracker.get(LIFESPAN);
    }

    public void setLifetime(int lifetime)
    {
        this.dataTracker.set(LIFESPAN, lifetime);
    }

    public byte getNum()
    {
        return this.dataTracker.get(Id);
    }

    public void setNum(byte ID)
    {
        this.dataTracker.set(Id, ID);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        UUID ownerUUID;
        if (nbt.containsUuid("Owner")) {
            ownerUUID = nbt.getUuid("Owner");
        } else {
            String string = nbt.getString("Owner");
            ownerUUID = ServerConfigHandler.getPlayerUuidByName(this.getServer(), string);
        }

        if (ownerUUID != null) {
            try {
                this.setOwnerUuid(ownerUUID);
            } catch (Throwable var4) {
                AureumAstaDisks.LOGGER.info("OwnerUUID missing.");
            }
        }

        this.setLifetime(nbt.getInt("Lifetime"));
        this.setNum(nbt.getByte("Num"));
    }

    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }

        nbt.putInt("Lifetime", this.getLifetime());
        nbt.putByte("Num", getNum());
    }

    public UUID getOwnerUuid() {
        return (UUID) ((Optional) this.dataTracker.get(OWNER_UUID)).orElse(null);
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    public void setOwner(PlayerEntity player) {
        this.setOwnerUuid(player.getUuid());
    }

    @Nullable
    @Override
    public LivingEntity getOwner() {
        try {
            UUID uUID = this.getOwnerUuid();
            return uUID == null ? null : this.getWorld().getPlayerByUuid(uUID);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        return false;
    }
}
