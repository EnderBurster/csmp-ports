package aureum.asta.disks.entity.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.entity.grimoire.goal.SharkAttackLogicGoal;
import aureum.asta.disks.entity.grimoire.goal.SharkDashSlashGoal;
import aureum.asta.disks.entity.grimoire.goal.SharkFollowOwnerGoal;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;
import aureum.asta.disks.ports.mason.entity.TameableHostileEntity;
import aureum.asta.disks.ports.mason.entity.goal.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.ServerConfigHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class SharkEntity extends HostileEntity implements GeoEntity, TameableHostileEntity {
    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    private static final TrackedData<Byte> TAMEABLE = DataTracker.registerData(SharkEntity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(SharkEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);
    private static final TrackedData<Integer> LIFESPAN = DataTracker.registerData(SharkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Integer> ATTACK_STATE = DataTracker.registerData(SharkEntity.class, TrackedDataHandlerRegistry.INTEGER);
    public static final TrackedData<Byte> Id = DataTracker.registerData(SharkEntity.class, TrackedDataHandlerRegistry.BYTE);

    public int dashSlashTicks = 0;

    public SharkEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new SharkMoveControl(this, this);
        this.setNum((byte) 0);
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(40);
    }

    public SharkEntity(EntityType<? extends HostileEntity> entityType, World world, int health, int identification) {
        super(entityType, world);
        this.moveControl = new SharkMoveControl(this, this);
        this.setNum((byte)identification);
        Objects.requireNonNull(this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).setBaseValue(health);
        this.setHealth(health);
    }

    @Override
    public boolean shouldDropXp() {
        return false;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean result = super.damage(source, amount);
        if (result && !this.world.isClient && getOwner() instanceof PlayerEntity player) {
            AureumAstaDisks.SHARKS.get(player).setSharkHealth(this.getHealth(), this.getNum());
        }
        return result;
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 40.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.35)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 9.0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private PlayState predicate(AnimationState<SharkEntity> sharkEntityAnimationState) {
        RawAnimation animationBuilder = RawAnimation.begin();

        if(sharkEntityAnimationState.isMoving())
        {
            animationBuilder.thenLoop("walk1");
        }
        if(this.getAttackState() == 2)
        {
            animationBuilder.thenLoop("sprint");
            animationBuilder.thenPlay("bite");
        }
        animationBuilder.thenLoop("idle");
        sharkEntityAnimationState.getController().setAnimation(animationBuilder);
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(TAMEABLE, (byte) 0);
        this.dataTracker.startTracking(OWNER_UUID, Optional.of(UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2")));
        this.dataTracker.startTracking(LIFESPAN, 200);
        this.dataTracker.startTracking(ATTACK_STATE, 0);
        this.dataTracker.startTracking(Id, (byte) 0);
    }

    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        return false;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(9, new LookAtEntityGoal(this, PlayerEntity.class, 3.0F, 1.0F));
        this.goalSelector.add(10, new LookAtEntityGoal(this, MobEntity.class, 8.0F));
        this.goalSelector.add(8, new WanderAroundFarGoal(this, 0.5f));
        this.goalSelector.add(3, new SharkAttackLogicGoal(this));
        this.goalSelector.add(2, new SharkDashSlashGoal(this));
        this.goalSelector.add(4, new SharkFollowOwnerGoal<>(this, 1, 5f, 2f, true));
        this.targetSelector.add(1, new TamedTrackAttackerGoal(this));
        this.targetSelector.add(2, new TamedAttackWithOwnerGoal<>(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, LivingEntity.class, 10, true, false, livingEntity -> (livingEntity instanceof HostileEntity) && !livingEntity.equals(this.getOwner()) && !(livingEntity instanceof TameableHostileEntity tameableHostileEntity && tameableHostileEntity.isOwner(this.getOwner()))));
    }

    @Override
    public void tick() {
        this.noClip = true;
        super.tick();
        //this.noClip = false;
        this.setNoGravity(true);

        int lifetime = this.getLifetime();

        if (lifetime < 0)
        {
            this.discard();
        }

        lifetime--;
        this.setLifetime(lifetime);

        if(this.getAttackState() == 2 && this.age % 2 == 0) {
            dashSlashTicks++;
        }
        if(dashSlashTicks >= 17) {
            setAttackState(0);
            dashSlashTicks = 0;
        }

        if (getTarget() != null && (!getTarget().isAlive() || getTarget().getHealth() <= 0)) setTarget(null);

        if(world.isClient)
        {
            for (int i = 0; i < 15; i++) {
                ParticleBuilders.create(AmariteParticles.AMARITE)
                        .setLifetime(12)
                        .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                        .setAlphaEasing(Easing.BOUNCE_IN_OUT)
                        .setColorCoefficient(2.0f)
                        .setColorEasing(Easing.BOUNCE_IN_OUT)
                        .setSpinEasing(Easing.SINE_IN)
                        .setScaleEasing(Easing.SINE_IN_OUT)
                        .setColor(0f, 0.45f, 1f, 0f, 0.078f, 0.788f, 0.8f)
                        .setAlpha(0.3f, 0.0f)
                        .setScale(0.1F + this.random.nextFloat()*0.05f, 0.02F)
                        .setSpin(this.random.nextBoolean() ? 0.1F : -0.1F)
                        .randomMotion(0.04, 0.01)
                        .setMotion(this.getVelocity().x * -1, this.getVelocity().y * -1, this.getVelocity().z * -1)
                        .randomOffset(5F, 2f, 3F)
                        .spawn(this.world, this.getX(), this.getY(), this.getZ());
            }
        }
    }

    @Override
    public boolean cannotDespawn() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @Override
    public boolean canFreeze() {
        return false;
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (this.getOwnerUuid() != null) {
            nbt.putUuid("Owner", this.getOwnerUuid());
        }

        nbt.putInt("Lifetime", this.getLifetime());
        nbt.putInt("AttackState", getAttackState());

        nbt.putInt("dashSlashTicks", dashSlashTicks);
        nbt.putByte("Num", getNum());
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
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
                this.setTamed(true);
            } catch (Throwable var4) {
                this.setTamed(false);
            }
        }

        this.setLifetime(nbt.getInt("Lifetime"));
        this.setAttackState(nbt.getInt("AttackState"));
        this.setNum(nbt.getByte("Num"));

        dashSlashTicks = nbt.getInt("dashSlashTicks");
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
    public UUID getOwnerUuid() {
        return (UUID) ((Optional) this.dataTracker.get(OWNER_UUID)).orElse(null);
    }

    public int getLifetime()
    {
        return this.dataTracker.get(LIFESPAN);
    }

    public void setLifetime(int lifetime)
    {
        this.dataTracker.set(LIFESPAN, lifetime);
    }

    @Override
    public void setOwnerUuid(@Nullable UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));
    }

    @Override
    public void setOwner(PlayerEntity player) {
        this.setTamed(true);
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

    @Override
    public boolean isOwner(LivingEntity entity) {
        return entity == this.getOwner();
    }

    @Override
    public boolean isTamed() {
        return (this.dataTracker.get(TAMEABLE) & 4) != 0;
    }

    @Override
    public void setTamed(boolean tamed) {
        byte b = this.dataTracker.get(TAMEABLE);
        if (tamed) {
            this.dataTracker.set(TAMEABLE, (byte) (b | 4));
        } else {
            this.dataTracker.set(TAMEABLE, (byte) (b & -5));
        }

        this.onTamedChanged();
    }

    public double getAngleBetweenEntities(Entity first, Entity second) {
        return Math.atan2(second.getZ() - first.getZ(), second.getX() - first.getX()) * (180 / Math.PI) + 90;
    }

    public int getAttackState() {
        return this.dataTracker.get(ATTACK_STATE);
    }

    public void setAttackState(int state) {
        this.dataTracker.set(ATTACK_STATE, state);
    }

    public class SharkMoveControl extends MoveControl {
        public SharkMoveControl(SharkEntity entity, SharkEntity owner) {
            super(owner);
        }

        public void tick() {
            if (this.state == State.MOVE_TO) {
                Vec3d vec3d = new Vec3d(this.targetX - SharkEntity.this.getX(), this.targetY - SharkEntity.this.getY(), this.targetZ - SharkEntity.this.getZ());
                double d = vec3d.length();
                if (d < SharkEntity.this.getBoundingBox().getAverageSideLength()) {
                    this.state = State.WAIT;
                    SharkEntity.this.setVelocity(SharkEntity.this.getVelocity().multiply((double)0.5F));
                } else {
                    SharkEntity.this.setVelocity(SharkEntity.this.getVelocity().add(vec3d.multiply(this.speed * 0.05 / d)));
                    if (SharkEntity.this.getTarget() == null) {
                        Vec3d vec3d2 = SharkEntity.this.getVelocity();
                        SharkEntity.this.setYaw(-((float) MathHelper.atan2(vec3d2.x, vec3d2.z)) * (180F / (float)Math.PI));
                        SharkEntity.this.bodyYaw = SharkEntity.this.getYaw();
                    } else {
                        double e = SharkEntity.this.getTarget().getX() - SharkEntity.this.getX();
                        double f = SharkEntity.this.getTarget().getZ() - SharkEntity.this.getZ();
                        SharkEntity.this.setYaw(-((float)MathHelper.atan2(e, f)) * (180F / (float)Math.PI));
                        SharkEntity.this.bodyYaw = SharkEntity.this.getYaw();
                    }
                }

            }
        }
    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return false;
    }
}
