package aureum.asta.disks.ports.mace.entity;

import aureum.asta.disks.ports.mace.FaithfulMace;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractWindChargeEntity extends ExplosiveProjectileEntity implements FlyingItemEntity {
   public static final ExplosionBehavior EXPLOSION_BEHAVIOR = new WindChargeNoDamageEntitiesExplosionBehavior();
   public static final double field_52224 = (double)0.25F;

   public AbstractWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> entityType, World world) {
      super(entityType, world);
      this.powerX = (double)0.0F;
      this.powerY = (double)0.0F;
      this.powerZ = (double)0.0F;
   }

   public AbstractWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> type, World world, Entity owner, double x, double y, double z) {
      super(type, x, y, z, (double)0.0F, (double)0.0F, (double)0.0F, world);
      this.setOwner(owner);
      this.powerX = (double)0.0F;
      this.powerY = (double)0.0F;
      this.powerZ = (double)0.0F;
   }

   AbstractWindChargeEntity(EntityType<? extends AbstractWindChargeEntity> entityType, double d, double e, double f, Vec3d vec3d, World world) {
      super(entityType, d, e, f, vec3d.x, vec3d.y, vec3d.z, world);
      this.powerX = (double)0.0F;
      this.powerY = (double)0.0F;
      this.powerZ = (double)0.0F;
   }

   public Box calculateBoundingBox() {
      Vec3d pos = this.getPos();
      float f = this.getType().getDimensions().width / 2.0F;
      float g = this.getType().getDimensions().height;
      float h = 0.15F;
      return new Box(pos.x - (double)f, pos.y - (double)0.15F, pos.z - (double)f, pos.x + (double)f, pos.y - (double)0.15F + (double)g, pos.z + (double)f);
   }

   public boolean collidesWith(Entity other) {
      return other instanceof AbstractWindChargeEntity ? false : super.collidesWith(other);
   }

   protected boolean canHit(Entity entity) {
      if (entity instanceof AbstractWindChargeEntity) {
         return false;
      } else {
         return entity.getType() == EntityType.END_CRYSTAL ? false : super.canHit(entity);
      }
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      World world = this.getWorld();
      if (world instanceof ServerWorld serverWorld) {
         Entity entity = this.getOwner();
         LivingEntity var10000;
         if (entity instanceof LivingEntity livingEntity) {
            var10000 = livingEntity;
         } else {
            var10000 = null;
         }

         LivingEntity livingEntity2 = var10000;
         Entity entity2 = entityHitResult.getEntity();
         if (livingEntity2 != null) {
            livingEntity2.onAttacking(entity2);
         }

         DamageSource damageSource = this.getDamageSources().create(FaithfulMace.MACE_SMASH, this, livingEntity2);
         if (entity2.damage(damageSource, 1.0F) && entity2 instanceof LivingEntity livingEntity3) {
            EnchantmentHelper.onTargetDamaged(livingEntity2, livingEntity3);
         }

         this.createExplosion(this.getPos());
      }

   }

   public void addVelocity(double deltaX, double deltaY, double deltaZ) {
   }

   protected abstract void createExplosion(Vec3d var1);

   protected void onBlockHit(BlockHitResult blockHitResult) {
      super.onBlockHit(blockHitResult);
      if (!this.getWorld().isClient) {
         Vec3i vec3i = blockHitResult.getSide().getVector();
         Vec3d vec3d = Vec3d.of(vec3i).multiply((double)0.25F, (double)0.25F, (double)0.25F);
         Vec3d vec3d2 = blockHitResult.getPos().add(vec3d);
         this.createExplosion(vec3d2);
         this.discard();
      }

   }

   protected void onCollision(HitResult hitResult) {
      super.onCollision(hitResult);
      if (!this.getWorld().isClient) {
         this.discard();
      }

   }

   protected boolean isBurning() {
      return false;
   }

   public ItemStack getStack() {
      return ItemStack.EMPTY;
   }

   protected float getDrag() {
      return 1.0F;
   }

   public boolean isTouchingWater() {
      return false;
   }

   protected @Nullable ParticleEffect getParticleType() {
      return null;
   }

   public void tick() {
      int topYInclusive = this.getWorld().getBottomY() + this.getWorld().getHeight() - 1;
      if (!this.getWorld().isClient && this.getBlockY() > topYInclusive + 30) {
         this.createExplosion(this.getPos());
         this.discard();
      } else {
         super.tick();
      }

   }
}
