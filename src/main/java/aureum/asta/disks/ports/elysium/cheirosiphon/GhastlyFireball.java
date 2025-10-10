package aureum.asta.disks.ports.elysium.cheirosiphon;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.ElysiumDamageSources;
import aureum.asta.disks.ports.elysium.ElysiumSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.AbstractFireballEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.NonnullDefault;

@NonnullDefault
public class GhastlyFireball extends AbstractFireballEntity {
   private int explosionPower = 1;

   public GhastlyFireball(EntityType<GhastlyFireball> entityType, World level) {
      super(entityType, level);
   }

   public GhastlyFireball(World level, LivingEntity livingEntity, double d, double e, double f, int i) {
      super(Elysium.GHASTLY_FIREBALL, livingEntity, d, e, f, level);
      this.explosionPower = i;
   }

   protected void onCollision(HitResult hitResult) {
      super.onCollision(hitResult);
      if (!this.world.isClient) {
         this.world.createExplosion(null, this.getX(), this.getY(), this.getZ(), (float)this.explosionPower, false, World.ExplosionSourceType.NONE);
         this.discard();
      }
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      super.onEntityHit(entityHitResult);
      if (!this.world.isClient) {
         Entity target = entityHitResult.getEntity();
         Entity owner = this.getOwner();
         target.damage(world.getDamageSources().create(ElysiumDamageSources.CHEIROSIPHON_GHASTLY_FIREBALL), 16.0F);
         if (owner instanceof LivingEntity) {
            this.applyDamageEffects((LivingEntity)owner, target);
         }
      }
   }

   public boolean damage(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         this.scheduleVelocityUpdate();
         Entity entity = source.getAttacker();
         if (entity != null && this.squaredDistanceTo(entity.getEyePos()) < 9.0) {
            if (!this.world.isClient) {
               Vec3d vec3 = entity.getRotationVector().multiply(4.0);
               if (entity instanceof ServerPlayerEntity player) {
                  player.networkHandler
                     .sendPacket(
                        new PlaySoundS2CPacket(
                                Registries.SOUND_EVENT.getEntry(ElysiumSounds.PARRY), SoundCategory.PLAYERS, this.getX(), this.getY(), this.getZ(), 2.0F, 1.0F, this.random.nextLong()
                        )
                     );
                  ((ServerWorld)this.world).spawnParticles(ParticleTypes.FLASH, this.getX(), this.getY(), this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
               }

               this.setVelocity(vec3);
               this.powerX = vec3.x * 0.1;
               this.powerY = vec3.y * 0.1;
               this.powerZ = vec3.z * 0.1;
               this.setOwner(entity);
               this.explosionPower = 3;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   @NotNull
   public ItemStack getStack() {
      ItemStack itemStack = this.getItem();
      return itemStack.isEmpty() ? new ItemStack(Elysium.GHASTLY_FIREBALL_ITEM) : itemStack;
   }

   protected boolean isBurning() {
      return false;
   }

   public void writeCustomDataToNbt(NbtCompound nbt) {
      super.writeCustomDataToNbt(nbt);
      nbt.putByte("ExplosionPower", (byte)this.explosionPower);
   }

   public void readCustomDataFromNbt(NbtCompound nbt) {
      super.readCustomDataFromNbt(nbt);
      if (nbt.contains("ExplosionPower", 99)) {
         this.explosionPower = nbt.getByte("ExplosionPower");
      }
   }

   public boolean shouldSave() {
      return false;
   }
}
