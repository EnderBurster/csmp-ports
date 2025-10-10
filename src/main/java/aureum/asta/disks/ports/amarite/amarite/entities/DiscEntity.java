package aureum.asta.disks.ports.amarite.amarite.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.DiscComponent;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteDamageTypes;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;

public class DiscEntity extends PersistentProjectileEntity {
   public static final float BASE_DAMAGE = 6.0F;
   public static final TrackedData<Byte> DISC_FLAGS = DataTracker.registerData(DiscEntity.class, TrackedDataHandlerRegistry.BYTE);
   public static final TrackedData<Integer> PLAYER_OWNER = DataTracker.registerData(DiscEntity.class, TrackedDataHandlerRegistry.INTEGER);
   public static final TrackedData<Integer> TRACKING_TICKS = DataTracker.registerData(DiscEntity.class, TrackedDataHandlerRegistry.INTEGER);
   public LivingEntity trackingTarget;
   private PlayerEntity playerOwner;
   public int pulledTime = 0;
   public boolean clockwise;
   public boolean orbit = false;
   public boolean pylon = false;
   public int durability = 3;

   public DiscEntity(EntityType<? extends DiscEntity> entityType, World world) {
      super(entityType, world);
   }

   protected void initDataTracker() {
      this.dataTracker.startTracking(DISC_FLAGS, (byte)0);
      this.dataTracker.startTracking(PLAYER_OWNER, 0);
      this.dataTracker.startTracking(TRACKING_TICKS, 0);
      super.initDataTracker();
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      if (!this.hasHit()) {
         PlayerEntity owner = this.getPlayerOwner();
         Entity target = entityHitResult.getEntity();
         target.timeUntilRegen = 0;
         if (target.damage(this.getWorld().getDamageSources().create(AmariteDamageTypes.A_DISC, this, this.getOwner()), BASE_DAMAGE)) {
            if (target.getType() == EntityType.ENDERMAN) {
               return;
            }

            this.setHit(true);
            if (owner != null) {
               DiscComponent discComponent = (DiscComponent)Amarite.DISC.get(owner);
               if (discComponent.damage(discComponent.getDiscIndex(this), 1) && !owner.isCreative()) {
                  this.playSound(SoundEvents.ENTITY_ITEM_BREAK, 1.0F, 1.6F);
                  this.setPosition(owner.getPos());
               }
            }

            if (target instanceof LivingEntity livingTarget) {
               if (owner != null) {
                  EnchantmentHelper.onUserDamaged(livingTarget, owner);
                  EnchantmentHelper.onTargetDamaged(owner, livingTarget);
               }

               this.onHit(livingTarget);
            }

            if (target != owner && target instanceof PlayerEntity && owner instanceof ServerPlayerEntity serverPlayer && !this.isSilent()) {
               serverPlayer.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.PROJECTILE_HIT_PLAYER, 0.0F));
            }

            this.playSound(AmariteSoundEvents.DISC_DAMAGE, 1.0F, 1.0F);
            Vec3d dif = target.getPos().add(0.0, (double)(target.getHeight() / 2.0F), 0.0).subtract(this.getPos()).normalize();
            dif = new Vec3d(Math.abs(dif.x), Math.abs(dif.y), Math.abs(dif.z));
            Vec3d power = this.getVelocity().multiply(dif);
            Vec3d remainingPower = this.getVelocity().subtract(power);
            power = power.multiply(-1.0).add(remainingPower);
            this.setVelocity(power);
         }
      }
   }

   protected void onBlockHit(BlockHitResult blockHitResult) {
      if (!this.isReturning()) {
         Direction direction = blockHitResult.getSide();
         this.setPosition(this.getPos().subtract(this.getVelocity()));
         switch (direction.getAxis()) {
            case X:
               this.setVelocity(this.getVelocity().multiply(-1.0, 1.0, 1.0));
               break;
            case Y:
               this.setVelocity(this.getVelocity().multiply(1.0, -1.0, 1.0));
               break;
            case Z:
               this.setVelocity(this.getVelocity().multiply(1.0, 1.0, -1.0));
         }

         this.playSound(this.getSound(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
      }
   }

   protected void onCollision(@NotNull HitResult hitResult) {
      if (hitResult.getType() != Type.MISS) {
         if ((Integer)this.dataTracker.get(TRACKING_TICKS) > 0) {
            return;
         }

         super.onCollision(hitResult);
         if ((this.pylon || this.orbit) && hitResult.getType() == Type.ENTITY && durability > 1)
         {
            this.setHit(false);
            return;
         }
         this.setReturning(true);
         this.clockwise = !this.clockwise;
      }
   }

   public static boolean isValidTarget(@NotNull DiscEntity entity, @Nullable Entity target) {
      PlayerEntity owner = entity.getPlayerOwner();
      if (target == owner) {
         return false;
      } else if (target instanceof LivingEntity livingTarget) {
         if (livingTarget.isDead()) {
            return false;
         } else if (target.isRemoved()) {
            return false;
         } else if (owner != null && target.isTeammate(owner)) {
            return false;
         } else {
            return !EntityPredicates.EXCEPT_CREATIVE_OR_SPECTATOR.test(target) ? false : target.canHit();
         }
      } else {
         return false;
      }
   }

   public void tick() {
      this.inGround = false;
      if (this.pulledTime > 0) {
         this.pulledTime--;
      }

      PlayerEntity owner = this.getPlayerOwner();
      if (owner == null) {
         this.removeDisc();
      } else {
         Vec3d pos = this.getPos();
         DiscComponent discComponent = (DiscComponent)Amarite.DISC.get(owner);
         if (this.isReturning()) {
            Vec3d ownerPos = owner.getEyePos();
            double distance = pos.distanceTo(ownerPos);
            if (distance < 2.0) {
               this.removeDisc();
               return;
            }

            double speedToReturn = Math.max(distance / 48.0, 0.25);
            Vec3d vec3d = owner.getEyePos().subtract(this.getPos()).normalize().multiply(speedToReturn);
            this.setVelocity(this.getVelocity().multiply(0.75).add(vec3d));
         }

         if (this.hasUsedRecall()) {
            Vec3d ownerPos = owner.getEyePos();
            double distance = pos.distanceTo(ownerPos);
            Vec3d oldPos = new Vec3d(this.lastRenderX, this.lastRenderY, this.lastRenderZ);
            double oldDistance = oldPos.distanceTo(ownerPos);
            if (distance < 2.0 || oldDistance < distance) {
               this.removeDisc();
               return;
            }
         }

         if (discComponent.orbitDuration > 0 && orbit) {
            Vec3d pos1 = owner.getPos().add(0.0, owner.getHeight() / 2.0F, 0.0);
            Vec3d pos2 = this.getPos();
            Vec3d difference = pos1.subtract(pos2).multiply(1.0, 0.0, 1.0).normalize();
            double angleInRadians = Math.toRadians(145.0);
            double cosTheta = Math.cos(angleInRadians);
            double sinTheta = Math.sin(angleInRadians);
            Vec3d spun = new Vec3d(
                    difference.x * cosTheta + difference.z * sinTheta,
                    difference.y,
                    -difference.x * sinTheta + difference.z * cosTheta
            );
            Vec3d targetPos = pos1.add(spun.multiply(1.0));
            double distPercent = 1.0 - pos1.distanceTo(pos2) / 2.0;
            this.setVelocity(
                    this.getVelocity().multiply(distPercent).add(targetPos.subtract(pos2).normalize().multiply(1.0 - distPercent))
            );
         } else if ((Integer)this.dataTracker.get(TRACKING_TICKS) > 0) {
            this.dataTracker.set(TRACKING_TICKS, (Integer)this.dataTracker.get(TRACKING_TICKS) - 1);
            if (this.world.isClient()) {
               this.world
                       .addParticle(
                               ParticleTypes.END_ROD,
                               this.getPos().x + this.random.nextFloat() - 0.5,
                               this.getPos().y + this.random.nextFloat() - 0.5,
                               this.getPos().z + this.random.nextFloat() - 0.5,
                               0.0,
                               0.0,
                               0.0
                       );
            }

            if (this.trackingTarget == null) {
               this.dataTracker.set(TRACKING_TICKS, 0);
            } else if ((Integer)this.dataTracker.get(TRACKING_TICKS) <= 0) {
               this.setNoGravity(true);
               this.setVelocity(
                       this.trackingTarget
                               .getPos()
                               .add(0.0, this.trackingTarget.getHeight() / 2.0F, 0.0)
                               .subtract(this.getPos().add(0.0, this.getHeight() / 2.0F, 0.0))
                               .normalize()
                               .multiply(3.0)
               );
            } else {
               Vec3d towards = this.trackingTarget
                       .getPos()
                       .add(0.0, this.trackingTarget.getHeight() / 2.0F, 0.0)
                       .subtract(this.getPos().add(0.0, this.getHeight() / 2.0F, 0.0))
                       .normalize()
                       .multiply(3.0);
               double d = towards.horizontalLength();
               this.setYaw((float)(MathHelper.atan2(towards.x, towards.z) * 180.0 / (float) Math.PI));
               this.setPitch((float)(MathHelper.atan2(towards.y, d) * 180.0 / (float) Math.PI));
               this.prevYaw = this.getYaw();
               this.prevPitch = this.getPitch();
            }
         } else if (this.pulledTime <= 0) {
            if (this.age > 80 && !this.isReturning()) {
               this.setReturning(true);
            }

            if (this.age > 400) {
               this.removeDisc();
            }
         }

         super.tick();
      }
   }

   private void removeDisc() {
      if (!this.world.isClient()) {
         PlayerEntity owner = this.getPlayerOwner();
         if (owner != null) {
            DiscComponent discComponent = (DiscComponent)Amarite.DISC.get(owner);
            discComponent.setDiscId(discComponent.getDiscIndex(this), -1);
         }

         this.discard();
      }
   }

   @Nullable
   protected EntityHitResult getEntityCollision(@NotNull Vec3d currentPosition, @NotNull Vec3d nextPosition) {
      EntityHitResult result = super.getEntityCollision(currentPosition.subtract(1.0, 2.0, 1.0), nextPosition.add(1.0, 2.0, 1.0));
      return result != null && isValidTarget(this, result.getEntity()) ? result : null;
   }

   protected ItemStack asItemStack() {
      return Items.AIR.getDefaultStack();
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

   protected SoundEvent getHitSound() {
      return AmariteSoundEvents.DISC_HIT;
   }

   protected float getDragInWater() {
      return 0.99F;
   }

   protected boolean tryPickup(PlayerEntity player) {
      return false;
   }

   public boolean hasHit() {
      return this.getDiscFlag(0);
   }

   public void setHit(boolean hit) {
      this.setDiscFlag(0, hit);
   }

   public boolean isReturning() {
      return this.getDiscFlag(1);
   }

   public void setReturning(boolean returning) {
      this.setDiscFlag(1, returning);
      this.setNoClip(returning);
   }

   public boolean hasUsedRecall() {
      return this.getDiscFlag(6);
   }

   public void setUsedRecall(boolean rebound) {
      this.setDiscFlag(6, rebound);
   }

   public boolean hasUsedRebound() {
      return this.getDiscFlag(7);
   }

   public void setUsedRebound(boolean rebounded) {
      this.setDiscFlag(7, rebounded);
   }

   private boolean getDiscFlag(int flag) {
      if (flag >= 0 && flag <= 8) {
         return ((Byte)this.dataTracker.get(DISC_FLAGS) >> flag & 1) == 1;
      } else {
         Amarite.LOGGER.warn("Invalid disc flag index: " + flag + " for disc " + this);
         return false;
      }
   }

   private void setDiscFlag(int flag, boolean value) {
      if (flag >= 0 && flag <= 8) {
         if (value) {
            this.dataTracker.set(DISC_FLAGS, (byte)((Byte)this.dataTracker.get(DISC_FLAGS) | 1 << flag));
         } else {
            this.dataTracker.set(DISC_FLAGS, (byte)((Byte)this.dataTracker.get(DISC_FLAGS) & ~(1 << flag)));
         }
      } else {
         Amarite.LOGGER.warn("Invalid disc flag index: " + flag + " for disc " + this);
      }
   }
}
