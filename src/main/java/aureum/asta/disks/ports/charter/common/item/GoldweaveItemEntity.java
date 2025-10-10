package aureum.asta.disks.ports.charter.common.item;

import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterPlayerComponent;
import aureum.asta.disks.ports.charter.common.component.GauntletMode;
import aureum.asta.disks.ports.charter.common.init.CharterEntities;
import aureum.asta.disks.ports.charter.common.util.CharterUtils;
import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.RaycastContext.FluidHandling;
import org.jetbrains.annotations.Nullable;

public class GoldweaveItemEntity extends ThrownItemEntity {
   public static final TrackedData<ItemStack> STACK = DataTracker.registerData(GoldweaveItemEntity.class, TrackedDataHandlerRegistry.ITEM_STACK);
   public ItemStack stack;
   public UUID ownerUUID;
   @Nullable
   public PlayerEntity owner;
   public int slot;
   public float damage;
   public int age;
   public int tickCooldown = 0;
   public int index;
   public boolean returning;
   public boolean thrown = false;
   private Vec3d prevPos;

   public boolean hasPortalCooldown() {
      return true;
   }

   public GoldweaveItemEntity(EntityType<? extends GoldweaveItemEntity> type, World world) {
      super(type, world);
      this.noClip = false;
   }

   public GoldweaveItemEntity(World world) {
      super(CharterEntities.GOLDWEAVE_ITEM, world);
      this.noClip = false;
   }

   public Vec3d getPrevPos() {
      return this.prevPos;
   }

   public boolean isAlive() {
      return true;
   }

   protected void onEntityHit(EntityHitResult entityHitResult) {
      this.getActualOwner();
      if (this.world.isClient || this.owner != null && !this.owner.isDead()) {
         DamageSource source = world.getDamageSources().mobProjectile(this, this.owner);
         Entity entity = entityHitResult.getEntity();
         if (!this.world.isClient) {
            if (!entity.equals(this.owner)) {
               boolean success = !entity.isInvulnerable();
               if (success) {
                  if (!this.world.isClient) {
                     if (entity instanceof LivingEntity livingentity && !entity.equals(this.owner)) {
                        this.stack.damage(1, this.owner, e -> this.remove(RemovalReason.KILLED));
                        int i = EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, this.stack);
                        if (i > 0) {
                           livingentity.setOnFireFor(i * 4);
                        }
                     }

                     entity.damage(
                        source,
                        (float)(
                           (double)(
                                 this.damage
                                    + EnchantmentHelper.getAttackDamage(
                                       this.stack, entity instanceof LivingEntity living ? living.getGroup() : EntityGroup.DEFAULT
                                    )
                              )
                              * this.getVelocity().length()
                        )
                     );
                  }

                  entity.world
                     .playSound(
                        null,
                        entity.getX(),
                        entity.getY(),
                        entity.getZ(),
                        SoundEvents.ENTITY_PLAYER_ATTACK_CRIT,
                        entity.getSoundCategory(),
                        1.0F,
                        0.9F + entity.world.random.nextFloat() * 0.2F
                     );
               }

               super.onEntityHit(entityHitResult);
            }
         }
      } else {
         ItemEntity entityitem = new ItemEntity(this.world, this.getX(), this.getY() + 0.5, this.getZ(), this.stack);
         entityitem.setPickupDelay(40);
         entityitem.setVelocity(entityitem.getVelocity().multiply(0.0, 1.0, 0.0));
         this.world.spawnEntity(entityitem);
         this.remove(RemovalReason.DISCARDED);
      }
   }

   public void tickGauntletMode() {
      if (this.owner != null) {
         switch (((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).mode) {
            case IDLE: {
               float angleSpacing = 1.2F;
               float angle = (float)(
                  (double)((float)this.age / 30.0F)
                     + (double)(this.index * angleSpacing / ((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).getListSize() * 2) * Math.PI
               );
               float radius = 1.5F;
               Vec3d vec = this.owner
                  .getPos()
                  .add(0.0, 1.5, 0.0)
                  .add(this.owner.getVelocity())
                  .add(
                     new Vec3d(0.5, radius * (double)MathHelper.sin(angle), radius * (double)MathHelper.cos(angle))
                        .rotateY((float)((double)(-this.owner.bodyYaw) * Math.PI / 180.0) + (float) (Math.PI / 2))
                  );
               Vec3d pos = this.getPos();
               this.setVelocity(vec.x - pos.x, vec.y - pos.y, vec.z - pos.z);
               break;
            }
            case SWEEP:
               if (!this.thrown) {
                  this.tickCooldown = 40
                     - (
                        8
                           + MathHelper.clamp(
                              Random.create((long)this.index)
                                    .nextInt(((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).getListSize())
                                 * 3,
                              0,
                              32
                           )
                     );
                  this.thrown = true;
               }

               if (this.tickCooldown > 40) {
                  HitResult hit = CharterUtils.hitscanEntity(
                     this.world, this.owner, 128.0, entity -> !(entity instanceof ProjectileEntity) && !entity.isSpectator()
                  );
                  if (hit == null) {
                     hit = CharterUtils.hitscanBlock(this.world, this.owner, 128.0, FluidHandling.NONE, block -> !block.equals(Blocks.AIR));
                  }

                  Vec3d targetPos = hit.getPos();
                  Vec3d motion = targetPos.subtract(this.getPos());
                  this.setVelocity(motion.normalize().multiply(0.98F));
                  if (this.squaredDistanceTo(targetPos) <= 1.0) {
                     this.tickCooldown = 0;
                  }
               } else {
                  float anglex = (float)(
                     (double)((float)this.age / 30.0F)
                        + (double)(this.index / ((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).getListSize() * 2)
                           * Math.PI
                  );
                  Vec3d vecx = this.owner
                     .getPos()
                     .add(0.0, 1.5, 0.0)
                     .add(this.owner.getVelocity())
                     .add(
                        new Vec3d(0.5, 1.5 * (double)MathHelper.sin(anglex), 1.5 * (double)MathHelper.cos(anglex))
                           .rotateY((float)((double)(-this.owner.bodyYaw) * Math.PI / 180.0) + (float) (Math.PI / 2))
                     );
                  Vec3d posx = this.getPos();
                  this.setVelocity(vecx.x - posx.x, vecx.y - posx.y, vecx.z - posx.z);
                  this.tickCooldown++;
               }
               break;
            case WHEEL: {
               float angle = (float)(
                  (double)((float)this.age / 4.5F)
                     + (double)(this.index / ((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).getListSize() * 2) * Math.PI
               );
               Vec3d vec = this.owner
                  .getPos()
                  .add(0.0, 1.5, 0.0)
                  .add(
                     new Vec3d(
                           (double)(4.0F * MathHelper.cos(angle)),
                           (double)(4.0F * MathHelper.sin(angle)),
                           this.index % 2 == 0 ? (double)(4.0F * MathHelper.sin(angle)) : (double)(-4.0F * MathHelper.sin(angle))
                        )
                        .rotateZ((float)((double)(-this.owner.getPitch()) * Math.PI / 180.0))
                        .rotateY((float)((double)(-this.owner.headYaw) * Math.PI / 180.0) + (float) (Math.PI / 2))
                  );
               this.setVelocity(vec.subtract(this.getPos()));
               break;
            }
            case SHIELD: {
               float angle = (float)this.age / 4.0F;
               Vec3d vec = this.owner
                  .getPos()
                  .add(0.0, 1.0, 0.0)
                  .add(this.owner.getVelocity())
                  .add(
                     new Vec3d((double)(2.0F * MathHelper.cos(angle)), 0.0, (double)(2.0F * MathHelper.sin(angle)))
                        .rotateX(
                           (float)((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).getGoldweaveList().indexOf(this) * 7.0F
                        )
                        .rotateZ(
                           (float)((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).getGoldweaveList().indexOf(this) * 7.0F
                        )
                  );
               Vec3d pos = this.getPos();
               this.setVelocity(vec.x - pos.x, vec.y - pos.y, vec.z - pos.z);
               break;
            }
            case BLADE: {
               Vec3d pos = this.getPos();
               Vec3d vec = this.owner.getPos().add(0.0, 1.0, 0.0).add(this.owner.getRotationVector().multiply((double)this.index * 0.5 + 1.0));
               this.setVelocity(
                  MathHelper.lerp(0.8 / (double)MathHelper.sqrt((float)(this.index + 1)), this.getVelocity().x, vec.x - pos.x),
                  MathHelper.lerp(0.8 / (double)MathHelper.sqrt((float)(this.index + 1)), this.getVelocity().y, vec.y - pos.y),
                  MathHelper.lerp(0.8 / (double)MathHelper.sqrt((float)(this.index + 1)), this.getVelocity().z, vec.z - pos.z)
               );
            }
            case COLLECT:
         }

         if (((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).mode != GauntletMode.SWEEP) {
            this.tickCooldown = 0;
            this.thrown = false;
         }
      }
   }

   public boolean canHit() {
      return false;
   }

   public void remove(RemovalReason reason) {
      if (this.owner != null && ((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).getGoldweaveList().contains(this)) {
         ((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).removeFromList(this);
      }

      super.remove(reason);
   }

   public void tick() {
      this.prevPos = this.getPos();
      super.tick();
      this.age++;
      this.getActualOwner();
      if (this.world.isClient || this.owner != null && !this.owner.isDead()) {
         if (this.world.isClient && this.stack != null) {
            if (!this.isInsideWaterOrBubbleColumn()) {
               if (EnchantmentHelper.getLevel(Enchantments.FIRE_ASPECT, this.getItem()) > 0) {
                  Vec3d vector = new Vec3d(this.getParticleX(0.2), this.getRandomBodyY(), this.getParticleZ(0.2));
                  this.world.addParticle(ParticleTypes.FLAME, vector.x, vector.y, vector.z, 0.0, 0.0, 0.0);
               }
            } else {
               Vec3d vector = new Vec3d(this.getParticleX(0.2), this.getRandomBodyY(), this.getParticleZ(0.2));
               this.world.addParticle(ParticleTypes.BUBBLE, vector.x, vector.y, vector.z, 0.0, 0.0, 0.0);
            }
         }

         if (!this.world.isClient) {
            if (this.prevPitch == 0.0F && this.prevYaw == 0.0F) {
               Vec3d vector3d = this.getVelocity();
               this.setYaw((float)(MathHelper.atan2(vector3d.x, vector3d.z) * 180.0F / (float)Math.PI));
               this.prevYaw = this.getYaw();
               this.prevPitch = this.getPitch();
            }

            this.tickGauntletMode();
            if (this.owner != null && ((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).mode == GauntletMode.COLLECT) {
               this.noClip = true;
               Vec3d ownerPos = this.owner.getPos().add(0.0, 1.0, 0.0);
               Vec3d motion = ownerPos.subtract(this.getPos());
               this.setVelocity(motion.normalize().multiply(0.75));
            }

            float distance = this.distanceTo(this.owner);
            if (((CharterPlayerComponent)this.owner.getComponent(CharterComponents.PLAYER_COMPONENT)).mode == GauntletMode.COLLECT
               && distance < 3.0F
               && this.isAlive()) {
               ItemScatterer.spawn(this.world, this.owner.getX(), this.owner.getY(), this.owner.getZ(), this.stack.copy());
               this.remove(RemovalReason.DISCARDED);
            }
         }
      } else {
         ItemEntity entityitem = new ItemEntity(this.world, this.getX(), this.getY() + 0.5, this.getZ(), this.stack);
         entityitem.setPickupDelay(40);
         entityitem.setVelocity(entityitem.getVelocity().multiply(0.0, 1.0, 0.0));
         this.world.spawnEntity(entityitem);
         this.remove(RemovalReason.DISCARDED);
      }
   }

   public PlayerEntity getActualOwner() {
      if (this.owner == null && this.world instanceof ServerWorld serverWorld) {
         this.owner = serverWorld.getEntity(this.ownerUUID) instanceof PlayerEntity playerEntity ? playerEntity : null;
      }

      return this.owner;
   }

   public void setData(float damage, UUID ownerUUID, int slot, ItemStack stack) {
      this.damage = damage;
      this.ownerUUID = ownerUUID;
      this.slot = slot;
      this.stack = stack;
   }

   public void writeCustomDataToNbt(NbtCompound nbt) {
      super.writeCustomDataToNbt(nbt);
      nbt.put("stack", this.stack.getOrCreateNbt());
      if (this.ownerUUID != null) {
         nbt.putUuid("ownerUUID", this.ownerUUID);
      }

      nbt.putInt("slot", this.slot);
      nbt.putFloat("damage", this.damage);
      nbt.putInt("age", this.age);
      nbt.putBoolean("returning", this.returning);
      nbt.putInt("tickCooldown", this.tickCooldown);
      nbt.putInt("index", this.index);
   }

   public void readCustomDataFromNbt(NbtCompound nbt) {
      super.readCustomDataFromNbt(nbt);
      if (nbt.contains("stack")) {
         this.stack = ItemStack.fromNbt(nbt.getCompound("stack"));
      }

      this.dataTracker.set(STACK, this.stack);
      if (nbt.contains("ownerUUID")) {
         this.ownerUUID = nbt.getUuid("ownerUUID");
         this.owner = this.getActualOwner();
      }

      this.slot = nbt.getInt("slot");
      this.damage = nbt.getFloat("damage");
      this.age = nbt.getInt("age");
      this.returning = nbt.getBoolean("returning");
      this.tickCooldown = nbt.getInt("tickCooldown");
      this.index = nbt.getInt("index");
   }

   public Packet<ClientPlayPacketListener> createSpawnPacket() {
      return new EntitySpawnS2CPacket(this);
   }

   protected Item getDefaultItem() {
      if (this.stack == null || this.stack.isEmpty()) {
         this.stack = (ItemStack)this.dataTracker.get(STACK);
      }

      return this.stack.getItem();
   }

   public ItemStack getItem() {
      if (this.stack == null || this.stack.isEmpty()) {
         this.stack = (ItemStack)this.dataTracker.get(STACK);
      }

      return this.stack;
   }

   public boolean isFireImmune() {
      return true;
   }

   public boolean isImmuneToExplosion() {
      return true;
   }

   public boolean hasNoGravity() {
      return true;
   }

   public float getTargetingMargin() {
      return 4.0F;
   }

   protected void initDataTracker() {
      super.initDataTracker();
      this.dataTracker.startTracking(STACK, ItemStack.EMPTY);
   }
}
