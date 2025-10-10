package aureum.asta.disks.ports.amarite.amarite.entities;


import aureum.asta.disks.ports.amarite.amarite.registry.AmariteDamageTypes;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEntities;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteParticles;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class MalignancyEntity extends Entity {
   public static final TrackedData<Float> POWER = DataTracker.registerData(MalignancyEntity.class, TrackedDataHandlerRegistry.FLOAT);
   public static final int TOTAL_DURATION = 320;
   public static final int ACTIVATION_TIME = 80;
   @Nullable
   private UUID ownerUuid;
   @Nullable
   private Entity owner;
   public boolean firing;
   @Environment(EnvType.CLIENT)
   public float rotation;

   public MalignancyEntity(EntityType<? extends MalignancyEntity> entityType, World world) {
      super(entityType, world);
      this.noClip = true;
   }

   public void tick() {
      super.tick();
      this.prevX = this.getX();
      this.prevY = this.getY();
      this.prevZ = this.getZ();
      Vec3d velocity = this.getVelocity();
      if (this.getVelocity().horizontalLengthSquared() > 1.0E-5F || (this.age + this.getId()) % 4 == 0) {
         this.move(MovementType.SELF, this.getVelocity());
         float g = 0.8F;
         this.setVelocity(this.getVelocity().multiply(g, g, g));
      }

      if (!this.world.isClient) {
         double d = this.getVelocity().subtract(velocity).lengthSquared();
         if (d > 0.01) {
            this.velocityDirty = true;
         }
      }

      if (this.age >= 80) {
         Float power = (Float)this.dataTracker.get(POWER);
         List<Entity> list = this.world
            .getOtherEntities(this, Box.of(this.getPos(), power, power, power));
         if (!this.world.isClient()) {
            for (Entity entity : list) {
               if (!this.isOwner(entity) && entity instanceof LivingEntity living) {
                  if (!living.hasStatusEffect(AmariteEntities.BUDDING)) {
                     living.addStatusEffect(new StatusEffectInstance(AmariteEntities.BUDDING, 48, 0, false, false, true));
                  }

                  living.timeUntilRegen = 0;
                  if (this.age % 40 == 0) {
                     entity.damage(entity.getDamageSources().create(AmariteDamageTypes.BUDDING), 3.0F);

                     for (Entity sub : this.world
                        .getOtherEntities(this, Box.of(entity.getPos(), power / 2.0F, power / 2.0F, power / 2.0F))) {
                        if (!this.isOwner(sub) && sub != entity && sub instanceof LivingEntity subTarget) {
                           if (!living.hasStatusEffect(AmariteEntities.BUDDING)) {
                              subTarget.addStatusEffect(new StatusEffectInstance(AmariteEntities.BUDDING, 48, 0, false, false, true));
                           }

                           subTarget.timeUntilRegen = 0;
                           sub.damage(sub.getDamageSources().create(AmariteDamageTypes.BUDDING), 1.0F);
                           subTarget.timeUntilRegen = 0;
                        }
                     }
                  }

                  living.timeUntilRegen = 0;
               }
            }
         } else {
            float r = 0.3647059F;
            float g = 0.65882355F;
            float b = 0.08627451F;
            ParticleBuilders.create(AmariteParticles.AMARITE)
               .setLifetime(32)
               .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
               .setAlpha(0.8F, 0.0F)
               .setAlphaEasing(Easing.CUBIC_IN)
               .setColorCoefficient(0.8F)
               .setColorEasing(Easing.CIRC_OUT)
               .setSpinEasing(Easing.SINE_IN)
               .setColor(r, g, b, 1.0F)
               .setScale(0.2F, 0.12F)
               .setSpinOffset(this.random.nextInt(360))
               .setSpin(this.random.nextBoolean() ? 0.1F : -0.1F)
               .setMotion(this.random.nextFloat() * 0.04 - 0.02, -0.016F, this.random.nextFloat() * 0.04 - 0.02)
               .randomOffset(this.getWidth() / 4.0F, this.getHeight())
               .spawn(this.world, this.getX(), this.getY(), this.getZ());
            ParticleBuilders.create(LodestoneParticles.SMOKE_PARTICLE)
               .setLifetime(32)
               .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
               .setAlphaEasing(Easing.BOUNCE_IN_OUT)
               .setColorCoefficient(0.8F)
               .setColorEasing(Easing.CIRC_OUT)
               .setSpinEasing(Easing.SINE_IN)
               .setColor((1.0F - r) / 2.0F, (1.0F - g) / 2.0F, (1.0F - b) / 2.0F, 0.1F)
               .setAlpha(0.4F, 0.0F)
               .setScale(1.0F + this.random.nextFloat(), 1.0F + this.random.nextFloat())
               .setSpinOffset(this.random.nextInt(360))
               .setSpin(this.random.nextBoolean() ? 0.1F : -0.1F)
               .randomMotion(0.04, 0.01)
               .randomOffset(power / 2.0F, power / 2.0F)
               .spawn(this.world, this.getX(), this.getY(), this.getZ());
            boolean shot = false;

            for (Entity entityx : list) {
               if (!this.isOwner(entityx) && entityx instanceof LivingEntity living) {
                  for (int i = 0; i < 6; i++) {
                     Vec3d pos = this.getPos()
                        .add(
                           this.random.nextGaussian() / 2.0,
                           this.random.nextGaussian() / 4.0 + this.getHeight() / 2.0F,
                           this.random.nextGaussian() / 2.0
                        );
                     Vec3d motion = living.getPos()
                        .add(0.0, living.getHeight() / 2.0F, 0.0)
                        .subtract(pos)
                        .normalize()
                        .multiply(0.4);
                     ParticleBuilders.create(AmariteParticles.ACCUMULATION)
                        .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                        .setLifetime(12)
                        .setAlpha(0.6F, 0.0F)
                        .setAlphaEasing(Easing.CUBIC_IN)
                        .setColorCoefficient(0.8F)
                        .setColorEasing(Easing.CIRC_OUT)
                        .setSpinEasing(Easing.SINE_IN)
                        .setColor(r, g, b, 1.0F)
                        .setScale(0.3F, 0.12F)
                        .setSpinOffset(this.random.nextInt(360))
                        .setSpin(this.random.nextBoolean() ? 0.5F : -0.5F)
                        .setMotion(motion.x, motion.y, motion.z)
                        .spawn(this.world, pos.x, pos.y, pos.z);
                  }

                  shot = true;

                  for (Entity subx : this.world
                     .getOtherEntities(this, Box.of(entityx.getPos(), power / 2.0F, power / 2.0F, power / 2.0F))) {
                     if (!this.isOwner(subx) && subx instanceof LivingEntity subTarget) {
                        for (int i = 0; i < 2; i++) {
                           Vec3d pos = entityx.getPos()
                              .add(
                                 this.random.nextGaussian() / 4.0,
                                 this.random.nextGaussian() / 6.0 + entityx.getHeight() / 2.0F,
                                 this.random.nextGaussian() / 4.0
                              );
                           Vec3d motion = subTarget.getPos()
                              .add(0.0, subTarget.getHeight() / 2.0F, 0.0)
                              .subtract(pos)
                              .normalize()
                              .multiply(0.3);
                           ParticleBuilders.create(AmariteParticles.ACCUMULATION)
                              .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                              .setLifetime(12)
                              .setAlpha(0.6F, 0.0F)
                              .setAlphaEasing(Easing.CUBIC_IN)
                              .setColorCoefficient(0.8F)
                              .setColorEasing(Easing.CIRC_OUT)
                              .setSpinEasing(Easing.SINE_IN)
                              .setColor(r, g, b, 1.0F)
                              .setScale(0.3F, 0.12F)
                              .setSpinOffset(this.random.nextInt(360))
                              .setSpin(this.random.nextBoolean() ? 0.5F : -0.5F)
                              .setMotion(motion.x, motion.y, motion.z)
                              .spawn(this.world, pos.x, pos.y, pos.z);
                        }
                     }
                  }
               }
            }

            this.firing = shot;
         }
      }

      if (this.age > 320) {
         if (!this.world.isClient()) {
            this.discard();
         } else {
            float r = 0.3647059F;
            float g = 0.65882355F;
            float b = 0.08627451F;

            for (int i = 0; i < 40; i++) {
               ParticleBuilders.create(AmariteParticles.AMARITE)
                  .setLifetime(64)
                  .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                  .setAlpha(1.0F, 0.0F)
                  .setAlphaEasing(Easing.CUBIC_IN)
                  .setColorCoefficient(0.8F)
                  .setColorEasing(Easing.CIRC_OUT)
                  .setSpinEasing(Easing.SINE_IN)
                  .setColor(r, g, b, 1.0F)
                  .setScale(0.2F, 0.12F)
                  .setSpinOffset(this.random.nextInt(360))
                  .setSpin(this.random.nextBoolean() ? 0.1F : -0.1F)
                  .setMotion(this.random.nextFloat() * 0.1 - 0.05, -0.016F, this.random.nextFloat() * 0.1 - 0.05)
                  .randomOffset(this.getWidth(), this.getHeight())
                  .spawn(this.world, this.getX(), this.getY(), this.getZ());
            }
         }
      }
   }

   public void setOwner(@Nullable Entity entity) {
      if (entity != null) {
         this.ownerUuid = entity.getUuid();
         this.owner = entity;
      }
   }

   protected boolean isOwner(@NotNull Entity entity) {
      return entity.getUuid().equals(this.ownerUuid);
   }

   @Nullable
   public Entity getOwner() {
      if (this.owner != null && !this.owner.isRemoved()) {
         return this.owner;
      } else {
         return this.ownerUuid != null && this.world instanceof ServerWorld
            ? (this.owner = ((ServerWorld)this.world).getEntity(this.ownerUuid))
            : null;
      }
   }

   protected void readCustomDataFromNbt(@NotNull NbtCompound nbt) {
      if (nbt.containsUuid("owner")) {
         this.ownerUuid = nbt.getUuid("owner");
      }
   }

   protected void writeCustomDataToNbt(NbtCompound nbt) {
      if (this.ownerUuid != null) {
         nbt.putUuid("owner", this.ownerUuid);
      }
   }

   @Override
   public Packet<ClientPlayPacketListener> createSpawnPacket() {
      Entity entity = this.getOwner();
      return new EntitySpawnS2CPacket(this, entity == null ? 0 : entity.getId());
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      Entity entity = this.world.getEntityById(packet.getEntityData());
      if (entity != null) {
         this.setOwner(entity);
      }
   }

   protected void initDataTracker() {
      this.dataTracker.startTracking(POWER, 8.0F);
   }
}
