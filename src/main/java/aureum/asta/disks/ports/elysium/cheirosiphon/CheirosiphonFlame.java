package aureum.asta.disks.ports.elysium.cheirosiphon;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.ElysiumDamageSources;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CheirosiphonFlame extends ThrownItemEntity {
   private static final TrackedData<Boolean> CONCENTRATED_ID = DataTracker.registerData(CheirosiphonFlame.class, TrackedDataHandlerRegistry.BOOLEAN);

   public CheirosiphonFlame(EntityType<? extends CheirosiphonFlame> entityType, World level) {
      super(entityType, level);
   }

   public CheirosiphonFlame(EntityType<? extends CheirosiphonFlame> entityType, double d, double e, double f, World level) {
      super(entityType, d, e, f, level);
   }

   public CheirosiphonFlame(EntityType<? extends CheirosiphonFlame> entityType, LivingEntity livingEntity, World level) {
      super(entityType, livingEntity, level);
      this.setPosition(this.getX(), this.getY() - (double)(this.getHeight() / 2.0F), this.getZ());
   }

   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.startTracking(CONCENTRATED_ID, false);
   }

   public void setConcentrated(boolean concentrated) {
      this.dataTracker.set(CONCENTRATED_ID, concentrated);
   }

   private boolean isConcentrated() {
      return (Boolean)this.dataTracker.get(CONCENTRATED_ID);
   }

   protected Item getDefaultItem() {
      return Items.FIRE_CHARGE;
   }

   protected void onCollision(HitResult hitResult) {
      Type type = hitResult.getType();
      if (type == Type.BLOCK) {
         BlockHitResult blockHitResult = (BlockHitResult)hitResult;
         this.onBlockHit(blockHitResult);
      }
   }

   protected void onBlockHit(BlockHitResult blockHitResult) {
      super.onBlockHit(blockHitResult);
      this.discard();
   }

   public boolean doesRenderOnFire() {
      return false;
   }

   public boolean isOnFire() {
      return true;
   }

   public boolean isFireImmune() {
      return true;
   }

   public boolean hasNoGravity() {
      return true;
   }

   public boolean isPushable() {
      return false;
   }

   public boolean isCollidable() {
      return false;
   }

   public void tick() {
      super.tick();
      if (this.age > (this.isConcentrated() ? 20 : 30)) {
         this.discard();
      }

      if (!this.world.isClient()) {
         this.world
            .getOtherEntities(this, this.getBoundingBox().expand(0.25), e -> !this.isOwner(e))
            .stream()
            .filter(LivingEntity.class::isInstance)
            .forEach(p -> {
               p.damage(this.world.getDamageSources().create(ElysiumDamageSources.CHEIROSIPHON), Math.max(1.5F, 6.0F - (float)this.age * 0.15F));
               if (!this.isWet()) {
                  p.setOnFireFor(2);
               }
            });
      } else {
         double concFactor = this.isConcentrated() ? 0.2 : 1.0;
         Vec3d particleSpeed = this.getVelocity()
            .multiply(this.random.nextDouble() * concFactor)
            .add(this.random.nextDouble() * concFactor * 0.1, this.random.nextDouble() * concFactor * 0.1, this.random.nextDouble() * concFactor * 0.1);
         this.world
            .addParticle(
               Elysium.ELYSIUM_FLAME_PARTICLE,
               this.getParticleX((double)this.getWidth()),
               this.getRandomBodyY(),
               this.getParticleZ((double)this.getWidth()),
               particleSpeed.getX(),
               particleSpeed.getY(),
               particleSpeed.getZ()
            );
         if (this.isInsideWaterOrBubbleColumn()) {
            this.world
               .addParticle(
                  ParticleTypes.BUBBLE,
                  this.getParticleX((double)this.getWidth()),
                  this.getRandomBodyY(),
                  this.getParticleZ((double)this.getWidth()),
                  this.getVelocity().getX() * this.random.nextDouble(),
                  this.getVelocity().getY() * this.random.nextDouble(),
                  this.getVelocity().getZ() * this.random.nextDouble()
               );
         }
      }
   }

   public void setVelocity(Entity user, float divergence, float speed) {
      float xRot = user.getPitch();
      float yRot = user.getYaw() + (this.random.nextFloat() - 0.5F) * divergence;
      Vec3d direction = this.getRotationVector(xRot, yRot).normalize();
      this.setVelocity(direction.multiply((double)speed));
      this.setPitch(xRot);
      this.setYaw(yRot);
   }
}
