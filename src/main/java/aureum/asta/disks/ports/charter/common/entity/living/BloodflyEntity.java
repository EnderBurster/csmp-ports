package aureum.asta.disks.ports.charter.common.entity.living;

import aureum.asta.disks.ports.charter.common.entity.living.goal.FindMobTargetGoal;
import java.util.EnumSet;
import java.util.UUID;

import aureum.asta.disks.ports.charter.common.interfaces.LockedTransport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.Goal.Control;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.attribute.DefaultAttributeContainer.Builder;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.FlyingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.Heightmap.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BloodflyEntity extends FlyingEntity implements Monster, LockedTransport {
   Vec3d targetPosition = Vec3d.ZERO;
   BlockPos circlingCenter = BlockPos.ORIGIN;
   BloodflyMovementType movementType = BloodflyMovementType.CIRCLE;
   @Nullable
   public UUID ownerUuid;

   public BloodflyEntity(EntityType<? extends BloodflyEntity> entityType, World world) {
      super(entityType, world);
      this.moveControl = new BloodflyMoveControl(this);
   }

   public void updatePassengerPosition(Entity passenger) {
      if (this.hasPassenger(passenger)) {
         Vec3d passangerPos = new Vec3d(this.getX(), this.getY(), this.getZ())
            .add(
               new Vec3d(0.0, -0.52F, 0.0)
                  .rotateX((float)this.getVelocity().length() * 0.6F)
                  .rotateY(-this.bodyYaw * (float) (Math.PI / 180.0) - (float) (Math.PI / 2))
            )
            .add(0.0, -1.62F, 0.0);
         passenger.setPosition(passangerPos);
         passenger.setYaw(MathHelper.clamp(passenger.getYaw(), this.getYaw() - 45.0F, this.getYaw() + 45.0F));
         passenger.setPitch(MathHelper.clamp(passenger.getPitch(), this.getPitch() - 70.0F, this.getPitch() + 70.0F));
         passenger.setHeadYaw(MathHelper.clamp(passenger.getHeadYaw(), this.getYaw() - 45.0F, this.getYaw() + 45.0F));
         if (passenger instanceof LivingEntity l) {
            passenger.setBodyYaw(MathHelper.clamp(l.bodyYaw, this.getYaw() - 10.0F, this.getYaw() + 10.0F));
         }
      }
   }

   protected void initGoals() {
      this.goalSelector.add(1, new StartAttackGoal());
      this.goalSelector.add(2, new SwoopMovementGoal());
      this.goalSelector.add(3, new CircleMovementGoal());
      this.targetSelector.add(1, new FindMobTargetGoal(this));
   }

   public void tick() {
      super.tick();
   }

   protected boolean isDisallowedInPeaceful() {
      return false;
   }

   public EntityGroup getGroup() {
      return EntityGroup.DEFAULT;
   }

   public static Builder createBloodflyAttributes() {
      return HostileEntity.createHostileAttributes()
         .add(EntityAttributes.GENERIC_MAX_HEALTH, 8.0)
         .add(EntityAttributes.GENERIC_ARMOR, 4.0)
         .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.0);
   }

   public boolean tryAttack(Entity target) {
      boolean bl = super.tryAttack(target);
      if (bl) {
         target.startRiding(this, true);
      }

      return bl;
   }

   public void readCustomDataFromNbt(NbtCompound nbt) {
      super.readCustomDataFromNbt(nbt);
      if (nbt.contains("owner")) {
         this.ownerUuid = nbt.getUuid("owner");
      }
   }

   public void writeCustomDataToNbt(NbtCompound nbt) {
      super.writeCustomDataToNbt(nbt);
      if (this.ownerUuid != null) {
         nbt.putUuid("owner", this.ownerUuid);
      }
   }

   /*public boolean removeStatusEffect(@NotNull StatusEffect type, @NotNull StatusEffectRemovalReason reason) {
      return this.removeStatusEffect(type, reason);
   }

   public int clearStatusEffects(@NotNull StatusEffectRemovalReason reason) {
      return this.clearStatusEffects(reason);
   }

   public void onStatusEffectRemoved(@NotNull StatusEffectInstance effect, @NotNull StatusEffectRemovalReason reason) {
      this.onStatusEffectRemoved(effect, reason);
   }*/

   class BloodflyMoveControl extends MoveControl {
      private float targetSpeed = 0.1F;

      public BloodflyMoveControl(MobEntity mobEntity) {
         super(mobEntity);
      }

      public void tick() {
         if (BloodflyEntity.this.horizontalCollision) {
            BloodflyEntity.this.setYaw(BloodflyEntity.this.getYaw() + 180.0F);
            this.targetSpeed = 0.1F;
         }

         double d = BloodflyEntity.this.targetPosition.x - BloodflyEntity.this.getX();
         double e = BloodflyEntity.this.targetPosition.y - BloodflyEntity.this.getY();
         double f = BloodflyEntity.this.targetPosition.z - BloodflyEntity.this.getZ();
         double g = Math.sqrt(d * d + f * f);
         if (Math.abs(g) > 1.0E-5F) {
            double h = 1.0 - Math.abs(e * 0.7F) / g;
            d *= h;
            f *= h;
            g = Math.sqrt(d * d + f * f);
            double i = Math.sqrt(d * d + f * f + e * e);
            float j = BloodflyEntity.this.getYaw();
            float k = (float)MathHelper.atan2(f, d);
            float l = MathHelper.wrapDegrees(BloodflyEntity.this.getYaw() + 90.0F);
            float m = MathHelper.wrapDegrees(k * 180.0F / (float) Math.PI);
            BloodflyEntity.this.setYaw(MathHelper.stepUnwrappedAngleTowards(l, m, 4.0F) - 90.0F);
            BloodflyEntity.this.bodyYaw = BloodflyEntity.this.getYaw();
            if (MathHelper.angleBetween(j, BloodflyEntity.this.getYaw()) < 3.0F) {
               this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 1.8F, 0.005F * (1.8F / this.targetSpeed));
            } else {
               this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 0.2F, 0.025F);
            }

            float n = (float)(-(MathHelper.atan2(-e, g) * 180.0 / (float) Math.PI));
            BloodflyEntity.this.setPitch(n);
            float o = BloodflyEntity.this.getYaw() + 90.0F;
            double p = (double)(this.targetSpeed * MathHelper.cos(o * (float) (Math.PI / 180.0))) * Math.abs(d / i);
            double q = (double)(this.targetSpeed * MathHelper.sin(o * (float) (Math.PI / 180.0))) * Math.abs(f / i);
            double r = (double)(this.targetSpeed * MathHelper.sin(n * (float) (Math.PI / 180.0))) * Math.abs(e / i);
            Vec3d vec3d = BloodflyEntity.this.getVelocity();
            BloodflyEntity.this.setVelocity(vec3d.add(new Vec3d(p, r, q).subtract(vec3d).multiply(0.3)));
         }
      }
   }

   static enum BloodflyMovementType {
      CIRCLE,
      SWOOP;
   }

   class CircleMovementGoal extends MovementGoal {
      private float angle;
      private float radius;
      private float yOffset;
      private float circlingDirection;

      public boolean canStart() {
         return BloodflyEntity.this.getTarget() == null || BloodflyEntity.this.movementType == BloodflyMovementType.CIRCLE;
      }

      public void start() {
         this.radius = 5.0F + BloodflyEntity.this.random.nextFloat() * 10.0F;
         this.yOffset = -4.0F + BloodflyEntity.this.random.nextFloat() * 9.0F;
         this.circlingDirection = BloodflyEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
         this.adjustDirection();
      }

      public void tick() {
         if (BloodflyEntity.this.random.nextInt(this.getTickCount(350)) == 0) {
            this.yOffset = -4.0F + BloodflyEntity.this.random.nextFloat() * 9.0F;
         }

         if (BloodflyEntity.this.random.nextInt(this.getTickCount(250)) == 0) {
            this.radius++;
            if (this.radius > 15.0F) {
               this.radius = 5.0F;
               this.circlingDirection = -this.circlingDirection;
            }
         }

         if (BloodflyEntity.this.random.nextInt(this.getTickCount(450)) == 0) {
            this.angle = BloodflyEntity.this.random.nextFloat() * 2.0F * (float) Math.PI;
            this.adjustDirection();
         }

         if (this.isNearTarget()) {
            this.adjustDirection();
         }

         if (BloodflyEntity.this.targetPosition.y < BloodflyEntity.this.getY() && !BloodflyEntity.this.world.isAir(BloodflyEntity.this.getBlockPos().down(1))) {
            this.yOffset = Math.max(1.0F, this.yOffset);
            this.adjustDirection();
         }

         if (BloodflyEntity.this.targetPosition.y > BloodflyEntity.this.getY() && !BloodflyEntity.this.world.isAir(BloodflyEntity.this.getBlockPos().up(1))) {
            this.yOffset = Math.min(-1.0F, this.yOffset);
            this.adjustDirection();
         }
      }

      private void adjustDirection() {
         if (BlockPos.ORIGIN.equals(BloodflyEntity.this.circlingCenter)) {
            BloodflyEntity.this.circlingCenter = BloodflyEntity.this.getBlockPos();
         }

         this.angle = this.angle + this.circlingDirection * 15.0F * (float) (Math.PI / 180.0);
         BloodflyEntity.this.targetPosition = Vec3d.of(BloodflyEntity.this.circlingCenter)
            .add((double)(this.radius * MathHelper.cos(this.angle)), (double)(-4.0F + this.yOffset), (double)(this.radius * MathHelper.sin(this.angle)));
      }
   }

   abstract class MovementGoal extends Goal {
      public MovementGoal() {
         this.setControls(EnumSet.of(Control.MOVE));
      }

      protected boolean isNearTarget() {
         return BloodflyEntity.this.targetPosition.squaredDistanceTo(BloodflyEntity.this.getX(), BloodflyEntity.this.getY(), BloodflyEntity.this.getZ()) < 4.0;
      }
   }

   class StartAttackGoal extends Goal {
      private int cooldown;

      public boolean canStart() {
         LivingEntity livingEntity = BloodflyEntity.this.getTarget();
         return livingEntity != null && BloodflyEntity.this.isTarget(livingEntity, TargetPredicate.DEFAULT);
      }

      public void start() {
         this.cooldown = this.getTickCount(10);
         BloodflyEntity.this.movementType = BloodflyMovementType.CIRCLE;
         this.startSwoop();
      }

      public void stop() {
         BloodflyEntity.this.circlingCenter = BloodflyEntity.this.world
            .getTopPosition(Type.MOTION_BLOCKING, BloodflyEntity.this.circlingCenter)
            .up(10 + BloodflyEntity.this.random.nextInt(20));
      }

      public void tick() {
         if (BloodflyEntity.this.movementType == BloodflyMovementType.CIRCLE) {
            this.cooldown--;
            if (this.cooldown <= 0) {
               BloodflyEntity.this.movementType = BloodflyMovementType.SWOOP;
               this.startSwoop();
               this.cooldown = this.getTickCount((8 + BloodflyEntity.this.random.nextInt(4)) * 20);
               BloodflyEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + BloodflyEntity.this.random.nextFloat() * 0.1F);
            }
         }
      }

      private void startSwoop() {
         BloodflyEntity.this.circlingCenter = BloodflyEntity.this.getTarget().getBlockPos().up(20 + BloodflyEntity.this.random.nextInt(20));
         if (BloodflyEntity.this.circlingCenter.getY() < BloodflyEntity.this.world.getSeaLevel()) {
            BloodflyEntity.this.circlingCenter = new BlockPos(
               BloodflyEntity.this.circlingCenter.getX(), BloodflyEntity.this.world.getSeaLevel() + 1, BloodflyEntity.this.circlingCenter.getZ()
            );
         }
      }
   }

   class SwoopMovementGoal extends MovementGoal {
      public boolean canStart() {
         return BloodflyEntity.this.getTarget() != null && BloodflyEntity.this.movementType == BloodflyMovementType.SWOOP;
      }

      public boolean shouldContinue() {
         LivingEntity livingEntity = BloodflyEntity.this.getTarget();
         if (livingEntity == null) {
            return false;
         } else if (!livingEntity.isAlive()) {
            return false;
         } else {
            if (livingEntity instanceof PlayerEntity playerEntity && (livingEntity.isSpectator() || playerEntity.isCreative())) {
               return false;
            }

            return this.canStart();
         }
      }

      public void start() {
      }

      public void stop() {
         BloodflyEntity.this.setTarget(null);
         BloodflyEntity.this.movementType = BloodflyMovementType.CIRCLE;
      }

      public void tick() {
         LivingEntity livingEntity = BloodflyEntity.this.getTarget();
         if (livingEntity != null) {
            BloodflyEntity.this.targetPosition = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5), livingEntity.getZ());
            if (BloodflyEntity.this.getBoundingBox().expand(0.2F).intersects(livingEntity.getBoundingBox())) {
               BloodflyEntity.this.tryAttack(livingEntity);
               BloodflyEntity.this.movementType = BloodflyMovementType.CIRCLE;
               if (!BloodflyEntity.this.isSilent()) {
                  BloodflyEntity.this.world.syncWorldEvent(1039, BloodflyEntity.this.getBlockPos(), 0);
               }
            } else if (BloodflyEntity.this.horizontalCollision || BloodflyEntity.this.hurtTime > 0) {
               BloodflyEntity.this.movementType = BloodflyMovementType.CIRCLE;
            }
         }
      }
   }
}
