package aureum.asta.disks.entity.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.damage.AstaDamageSources;
import aureum.asta.disks.effect.AstaStatusEffects;
import aureum.asta.disks.init.AstaEntities;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.DiscComponent;
import aureum.asta.disks.ports.amarite.amarite.entities.DiscEntity;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;
import aureum.asta.disks.ports.charter.Charter;
import aureum.asta.disks.ports.mason.entity.SoulmouldEntity;
import aureum.asta.disks.ports.mason.entity.TameableHostileEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Random;
import java.util.UUID;

public class AquabladeEntity extends PersistentProjectileEntity {
    public static final TrackedData<Integer> PLAYER_OWNER = DataTracker.registerData(AquabladeEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private PlayerEntity playerOwner;
    private float orbitAngle = 0;
    private final float orbitDistance = 8.0f;
    private int maxAge = 200;

    public AquabladeEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public AquabladeEntity(World world, LivingEntity owner) {
        super(AstaEntities.GRIMOIRE_AQUABLADE, owner, world);
    }

    protected void initDataTracker()
    {
        this.dataTracker.startTracking(PLAYER_OWNER, 0);
        super.initDataTracker();
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    public void tick()
    {
        PlayerEntity owner = this.getPlayerOwner();

        if (this.world.isClient && this.getOwner() instanceof PlayerEntity player)
        {
            spawnOrbitParticles();
            return;
        }

        if (owner == null || this.age == maxAge) {
            this.removeDisc();
            return;
        }

        this.orbitAngle += 0.2f;

        double centerX = owner.getX();
        double centerY = owner.getY() + (owner.getHeight() / 2.0F);
        double centerZ = owner.getZ();

        double offsetX = orbitDistance * Math.cos(orbitAngle);
        double offsetZ = orbitDistance * Math.sin(orbitAngle);

        this.setPos(centerX + offsetX, centerY, centerZ + offsetZ);

        for (LivingEntity livingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox(), livingEntity -> !livingEntity.equals(this.getOwner()) && !(livingEntity instanceof TameableEntity tamed && tamed.getOwner() != null && tamed.getOwner().equals(this.getOwner())) && !(livingEntity instanceof ArmorStandEntity) && !(livingEntity instanceof TameableHostileEntity tameableHostile && tameableHostile.isOwner((LivingEntity) this.getOwner())) && !(livingEntity instanceof PlayerEntity player && Charter.bannedUuids.contains(player.getUuid())))) {
            livingEntity.damage(this.getWorld().getDamageSources().create(AstaDamageSources.BLEED, this, this.getOwner()), 4.0F);
            livingEntity.addStatusEffect(new StatusEffectInstance(AstaStatusEffects.BLEED, 20*3, 1));
        }

        super.tick();
    }

    @Nullable
    public PlayerEntity getPlayerOwner() {
        if (this.world.isClient()) {
            return null;
        } else if (this.playerOwner != null) {
            return this.playerOwner;
        } else {
            int id = (Integer)this.dataTracker.get(PLAYER_OWNER);
            if (id != -1 && this.world.getEntityById(id) instanceof PlayerEntity player) {
                this.playerOwner = player;
                return player;
            } else {
                this.discard();
                return null;
            }
        }
    }

    public void setOwner(@Nullable Entity entity) {
        if (entity instanceof PlayerEntity player) {
            this.dataTracker.set(PLAYER_OWNER, player.getId());
        } else {
            this.dataTracker.set(PLAYER_OWNER, -1);
        }

        super.setOwner(entity);
    }

    private void spawnOrbitParticles() {
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
                    .spawn(this.world, this.getX(), this.getY(), this.getZ());

            /*ParticleBuilders.create(LodestoneParticles.SMOKE_PARTICLE)
                    .setLifetime(1)
                    .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                    .setAlphaEasing(Easing.BOUNCE_IN_OUT)
                    .setSpinEasing(Easing.SINE_IN)
                    .setScaleEasing(Easing.SINE_IN_OUT)
                    .setColor(1f, 1f, 1f, 0.5f)
                    .setAlpha(0.6f, 0.0f)
                    .setScale(0.2F - this.random.nextFloat()*0.05f, 0.01F)
                    .setSpin(this.random.nextBoolean() ? 0.1F : -0.1F)
                    .randomMotion(0.04, 0.2)
                    .addMotion((this.getX()-this.prevX), 0, (this.getZ()-this.prevZ))
                    .randomOffset(0.2)
                    .setGravity(3f)
                    .spawnCircle(this.world, this.getX(), this.getY(), this.getZ(), 1.2, i, count);*/
        }
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {}

    @Override
    public boolean hasNoGravity(){return true;}

    protected boolean tryPickup(PlayerEntity player) {
        return false;
    }

    public void setOrbitAngle(float orbitAngleNew)
    {
        this.orbitAngle = orbitAngleNew;
    }

    private void removeDisc() {
        if (!this.world.isClient()) {
            this.discard();
        }
    }

    public void setMaxAge(int age)
    {
        this.maxAge = age;
    }
}
