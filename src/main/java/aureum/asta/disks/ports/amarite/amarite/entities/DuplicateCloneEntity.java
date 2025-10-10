package aureum.asta.disks.ports.amarite.amarite.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.Arm;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.text.Text;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.data.DataTracker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import aureum.asta.disks.ports.amarite.amarite.Amarite;

public class DuplicateCloneEntity extends LivingEntity {
   public static final float BASE_HEALTH = 3.0F;
   public static final TrackedData<Byte> CLONE_FLAGS = DataTracker.registerData(DuplicateCloneEntity.class, TrackedDataHandlerRegistry.BYTE);
   public static final TrackedData<Integer> PLAYER_OWNER = DataTracker.registerData(DuplicateCloneEntity.class, TrackedDataHandlerRegistry.INTEGER);
   private PlayerEntity playerOwner;
   public int lifeTime = 0;
   public boolean running;

   public DuplicateCloneEntity(EntityType<? extends DuplicateCloneEntity> entityType, World world) {
      super(entityType, world);
   }

   protected void initDataTracker() {
      this.dataTracker.startTracking(CLONE_FLAGS, (byte)0);
      this.dataTracker.startTracking(PLAYER_OWNER, 0);
      super.initDataTracker();
   }

   public Iterable<ItemStack> getArmorItems() {
      return null;
   }

   public ItemStack getEquippedStack(EquipmentSlot slot) {
      return null;
   }

   public void equipStack(EquipmentSlot slot, ItemStack stack) {
   }

   public static boolean isValidTarget(@NotNull DuplicateCloneEntity entity, @Nullable Entity target) {
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
      if (this.lifeTime > 0) {
         this.lifeTime--;
      }

      PlayerEntity owner = this.getPlayerOwner();
      if (owner == null) {
         this.despawnDupe();
      } else {
         Vec3d pos = this.getPos();
         if (this.isReturning()) {
            Vec3d ownerPos = owner.getEyePos();
            double distance = pos.distanceTo(ownerPos);
            if (distance < 2.0) {
               this.despawnDupe();
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
               this.despawnDupe();
               return;
            }
         }

         if (this.lifeTime <= 0) {
            if (this.age > 80 && !this.isReturning()) {
               this.setReturning(true);
            }

            if (this.age > 400) {
               this.despawnDupe();
            }
         }

         super.tick();
      }
   }

   public Arm getMainArm() {
      return null;
   }

   private void despawnDupe() {
      if (!this.world.isClient()) {
         PlayerEntity owner = this.getPlayerOwner();
         if (owner != null) {
            owner.sendMessage(Text.literal("The clone perished"), true);
         }

         this.discard();
      }
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

   public boolean hasHit() {
      return this.getDupeFlag(0);
   }

   public void setHit(boolean hit) {
      this.setDupeFlag(0, hit);
   }

   public boolean isReturning() {
      return this.getDupeFlag(1);
   }

   public void setReturning(boolean returning) {
      this.setDupeFlag(1, returning);
   }

   public boolean hasUsedRecall() {
      return this.getDupeFlag(6);
   }

   public void setUsedRecall(boolean rebound) {
      this.setDupeFlag(6, rebound);
   }

   public boolean hasUsedRebound() {
      return this.getDupeFlag(7);
   }

   public void setUsedRebound(boolean rebounded) {
      this.setDupeFlag(7, rebounded);
   }

   private boolean getDupeFlag(int flag) {
      if (flag >= 0 && flag <= 8) {
         return ((Byte)this.dataTracker.get(CLONE_FLAGS) >> flag & 1) == 1;
      } else {
         Amarite.LOGGER.warn("Invalid disc flag index: " + flag + " for disc " + this);
         return false;
      }
   }

   private void setDupeFlag(int flag, boolean value) {
      if (flag >= 0 && flag <= 8) {
         if (value) {
            this.dataTracker.set(CLONE_FLAGS, (byte)((Byte)this.dataTracker.get(CLONE_FLAGS) | 1 << flag));
         } else {
            this.dataTracker.set(CLONE_FLAGS, (byte)((Byte)this.dataTracker.get(CLONE_FLAGS) & ~(1 << flag)));
         }
      } else {
         Amarite.LOGGER.warn("Invalid disc flag index: " + flag + " for disc " + this);
      }
   }
}
