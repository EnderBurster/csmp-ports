package aureum.asta.disks.mixin.ports.mace;

import aureum.asta.disks.ports.mace.PlayerEntityMaceInterface;
import aureum.asta.disks.ports.mace.item.MaceItem;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin({PlayerEntity.class})
public abstract class PlayerEntityInterfaceMixin implements PlayerEntityMaceInterface {
   @Unique
   public @Nullable Vec3d currentExplosionImpactPos;
   @Unique
   public @Nullable Entity explodedBy;
   @Unique
   private boolean ignoreFallDamageFromCurrentExplosion;
   @Unique
   private int currentExplosionResetGraceTime;
   @Unique
   private boolean spawnExtraParticlesOnFall;

   public PlayerEntityInterfaceMixin() {
   }

   @Inject(
           method = {"attack"},
           at = {@At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/enchantment/EnchantmentHelper;getKnockback(Lnet/minecraft/entity/LivingEntity;)I"
           )}
   )
   private void addMaceDamage(Entity target, CallbackInfo ci, @Local(ordinal = 0) LocalFloatRef f) {
      ItemStack itemStack = ((PlayerEntity)(Object)this).getMainHandStack();
      PlayerEntity thiscast = (PlayerEntity)(Object)this;
      if (target instanceof LivingEntity && itemStack.getItem() instanceof MaceItem) {
         MaceItem item = (MaceItem)itemStack.getItem();
         DamageSource damageSource = (DamageSource) Optional.ofNullable(item.getDamageSource(thiscast)).orElse(thiscast.getDamageSources().playerAttack(thiscast));
         float extra = ((MaceItem)itemStack.getItem()).getBonusAttackDamage(target, f.get(), damageSource);
         f.set(f.get() + extra);
      }

   }

   public void setCurrentExplosionImpactPos(Vec3d newVal) {
      this.currentExplosionImpactPos = newVal;
   }

   public Vec3d currentExplosionImpactPos() {
      return this.currentExplosionImpactPos;
   }

   public void setExplodedBy(Entity newVal) {
      this.explodedBy = newVal;
   }

   public Entity explodedBy() {
      return this.explodedBy;
   }

   public void setIgnoreFallDamageFromCurrentExplosion(boolean newVal) {
      this.ignoreFallDamageFromCurrentExplosion = newVal;
   }

   public boolean ignoreFallDamageFromCurrentExplosion() {
      return this.ignoreFallDamageFromCurrentExplosion;
   }

   public void setCurrentExplosionResetGraceTime(int newVal) {
      this.currentExplosionResetGraceTime = newVal;
   }

   public int currentExplosionResetGraceTime() {
      return this.currentExplosionResetGraceTime;
   }

   public void setSpawnExtraParticlesOnFall(boolean newVal) {
      this.spawnExtraParticlesOnFall = newVal;
   }

   public boolean spawnExtraParticlesOnFall() {
      return this.spawnExtraParticlesOnFall;
   }
}
