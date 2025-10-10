package aureum.asta.disks.ports.mace.item;

import aureum.asta.disks.init.AstaEntityComponents;
import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.MaceUtil;
import aureum.asta.disks.ports.mace.enchantments.MaceEnchantmentHelperPort;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class MaceItem extends Item {
   private static final int ATTACK_DAMAGE_MODIFIER_VALUE = 5;
   private static final float ATTACK_SPEED_MODIFIER_VALUE = -3.4F;
   public static final float MINING_SPEED_MULTIPLIER = 1.5F;
   private static final float HEAVY_SMASH_SOUND_FALL_DISTANCE_THRESHOLD = 5.0F;
   public static final float KNOCKBACK_RANGE = 3.5F;
   private static final float KNOCKBACK_POWER = 0.7F;
   private static final boolean rebalance = true;
   private final float attackDamage = 5.0F;
   private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

   public MaceItem(Item.Settings settings) {
      super(settings);
      float speed = rebalance ? -3.2F : -3.4F;
      Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
      builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)speed, EntityAttributeModifier.Operation.ADDITION));
      builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)this.attackDamage, EntityAttributeModifier.Operation.ADDITION));
      this.attributeModifiers = builder.build();
   }

   public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
      return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getAttributeModifiers(slot);
   }

   public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
      return !miner.isCreative();
   }

   public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      float fallDist = attacker.fallDistance;
      boolean bl6 = false;
      if (target.getWorld() instanceof ServerWorld) {
         bl6 = this.internalPostHit(stack, target, attacker);
      }

      if (!attacker.getWorld().isClient && !stack.isEmpty() && target instanceof LivingEntity && bl6) {
         this.internalPostDamageEntity(stack, target, attacker);
      }

      MaceEnchantmentHelperPort.forEachEnchantAfterPostHitMaceForWindBurst(stack, attacker, fallDist);
      return true;
   }

   private boolean internalPostHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      if (shouldDealAdditionalDamage(attacker)) {
         ServerWorld serverWorld = (ServerWorld)attacker.getWorld();
         attacker.setVelocity(attacker.getVelocity().withAxis(Direction.Axis.Y, (double)0.01F));
         if (attacker instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)attacker;
            serverPlayerEntity.setCurrentExplosionImpactPos(this.getCurrentExplosionImpactPos(serverPlayerEntity));
            serverPlayerEntity.setIgnoreFallDamageFromCurrentExplosion(true);
            serverPlayerEntity.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayerEntity));
         }

         if (target.isOnGround()) {
            if (attacker instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)attacker;
               serverPlayerEntity.setSpawnExtraParticlesOnFall(true);
            }

            SoundEvent soundEvent = attacker.fallDistance > 5.0F ? FaithfulMace.ITEM_MACE_SMASH_GROUND_HEAVY_SOUND_EVENT : FaithfulMace.ITEM_MACE_SMASH_GROUND_SOUND_EVENT;
            serverWorld.playSound((PlayerEntity)null, attacker.getX(), attacker.getY(), attacker.getZ(), soundEvent, attacker.getSoundCategory(), 1.0F, 1.0F);
         } else {
            serverWorld.playSound((PlayerEntity)null, attacker.getX(), attacker.getY(), attacker.getZ(), FaithfulMace.ITEM_MACE_SMASH_AIR_SOUND_EVENT, attacker.getSoundCategory(), 1.0F, 1.0F);
         }

         knockbackNearbyEntities(serverWorld, attacker, target);
      }

      return true;
   }

   private Vec3d getCurrentExplosionImpactPos(ServerPlayerEntity player) {
      return player.ignoreFallDamageFromCurrentExplosion() && player.currentExplosionImpactPos() != null && player.currentExplosionImpactPos().y <= player.getPos().y ? player.currentExplosionImpactPos() : player.getPos();
   }

   private void internalPostDamageEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
      MaceUtil.itemStackDamage(stack, 1, attacker, EquipmentSlot.MAINHAND);
      if (shouldDealAdditionalDamage(attacker)) {
         attacker.onLanding();
      }

   }

   @Override
   public float getBonusAttackDamage(Entity target, float baseAttackDamage, DamageSource damageSource) {
      Entity var5 = damageSource.getSource();
      if (var5 instanceof LivingEntity livingEntity) {
         if (!shouldDealAdditionalDamage(livingEntity)) {
            return 0.0F;
         }
         else
         {
            if (AstaEntityComponents.METEOR.get(livingEntity).hasMeteor())
            {
               target.setOnFireFor(AstaEntityComponents.METEOR.get(livingEntity).getFireDuration());
            }

            if (rebalance)
            {
               float damage = (float) (6 * Math.log(livingEntity.fallDistance + 1));
               if (target.getWorld() instanceof ServerWorld serverWorld) {
                  float bonus = MaceEnchantmentHelperPort.getSmashDamagePerFallenBlock(serverWorld, livingEntity.getMainHandStack(), target, damageSource, 0);
                  return damage + bonus * 2;
               }
               return damage;
            }

            float h = livingEntity.fallDistance;
            float i;
            if (h <= 3.0F) {
               i = 4.0F * h;
            } else if (h <= 8.0F) {
               i = 12.0F + 2.0F * (h - 3.0F);
            } else {
               i = 22.0F + h - 8.0F;
            }

            World var10 = livingEntity.getWorld();
            if (var10 instanceof ServerWorld) {
               ServerWorld serverWorld = (ServerWorld)var10;
               return i + MaceEnchantmentHelperPort.getSmashDamagePerFallenBlock(serverWorld, livingEntity.getMainHandStack(), target, damageSource, 0.0F) * h;
            } else {
               return i;
            }
         }
      } else {
         return 0.0F;
      }
   }

   private static void knockbackNearbyEntities(World world, Entity attacker, Entity attacked) {
      world.syncWorldEvent(2013, attacked.getSteppingPos(), 750);
      world.getEntitiesByClass(LivingEntity.class, attacked.getBoundingBox().expand((double)3.5F), getKnockbackPredicate(attacker, attacked)).forEach((entity) -> {
         Vec3d vec3d = entity.getPos().subtract(attacked.getPos());
         double d = getKnockback(attacker, entity, vec3d);
         Vec3d vec3d2 = vec3d.normalize().multiply(d);
         if (d > (double)0.0F) {
            entity.addVelocity(vec3d2.x, (double)0.7F, vec3d2.z);
            if (entity instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
               serverPlayerEntity.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayerEntity));
            }
         }

      });
   }

   private static Predicate<LivingEntity> getKnockbackPredicate(Entity attacker, Entity attacked) {
      return (entity) -> {
         boolean var10000;
         boolean bl;
         boolean bl2;
         boolean bl3;
         label62: {
            bl = !entity.isSpectator();
            bl2 = entity != attacker && entity != attacked;
            bl3 = !attacker.isTeammate(entity);
            if (entity instanceof TameableEntity tameableEntity) {
               if (tameableEntity.isTamed() && attacker.getUuid().equals(tameableEntity.getOwnerUuid())) {
                  var10000 = true;
                  break label62;
               }
            }

            var10000 = false;
         }

         boolean bl4;
         label55: {
            bl4 = !var10000;
            if (entity instanceof ArmorStandEntity armorStandEntity) {
               if (armorStandEntity.isMarker()) {
                  var10000 = false;
                  break label55;
               }
            }

            var10000 = true;
         }

         boolean bl6 = attacked.squaredDistanceTo(entity) <= Math.pow((double)3.5F, (double)2.0F);
         return bl && bl2 && bl3 && bl4 && var10000 && bl6;
      };
   }

   private static double getKnockback(Entity attacker, LivingEntity attacked, Vec3d distance) {
      return ((double)3.5F - distance.length()) * (double)0.7F * (double)(attacker.fallDistance > 5.0F ? 2 : 1) * ((double)1.0F - attacked.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE));
   }

   public static boolean shouldDealAdditionalDamage(LivingEntity attacker) {
      return attacker.fallDistance > 1.5F && !attacker.isFallFlying();
   }

   public @Nullable DamageSource getDamageSource(LivingEntity user) {
      return shouldDealAdditionalDamage(user) ? user.getDamageSources().create(FaithfulMace.MACE_SMASH, user) : null;
   }
}

