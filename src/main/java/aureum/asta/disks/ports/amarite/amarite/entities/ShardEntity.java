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
import net.minecraft.util.math.Vec3d;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteDamageTypes;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;

public class ShardEntity extends PersistentProjectileEntity {
   public static final float BASE_DAMAGE = 3.0F;
   public LivingEntity trackingTarget;

   public ShardEntity(EntityType<? extends ShardEntity> entityType, World world) {
      super(entityType, world);
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      PlayerEntity owner = this.getPlayerOwner();
      Entity target = entityHitResult.getEntity();
      target.timeUntilRegen = 0;
      if (this.trackingTarget != null) {
         this.trackingTarget.timeUntilRegen = 0;
      }

      if (target.damage(this.getWorld().getDamageSources().create(AmariteDamageTypes.A_DISC, this, this.getOwner()), 3.0F)) {
         if (target.getType() == EntityType.ENDERMAN) {
            return;
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
         this.discard();
      }
   }

   protected void onBlockHit(BlockHitResult blockHitResult) {
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

   protected void onCollision(@NotNull HitResult hitResult) {
      if (hitResult.getType() != Type.MISS) {
         super.onCollision(hitResult);
      }
   }

   public static boolean isValidTarget(@NotNull ShardEntity entity, @Nullable Entity target) {
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
      PlayerEntity owner = this.getPlayerOwner();
      if (owner == null) {
         this.removeShard();
      } else {
         super.tick();
      }
   }

   private void removeShard() {
      if (!this.world.isClient()) {
         this.getPlayerOwner();
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
      } else {
         this.discard();
         return null;
      }
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
}
