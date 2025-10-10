package aureum.asta.disks.mixin.ports.amarite.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({MobEntity.class})
public abstract class MobEntityMixin extends LivingEntity {
   protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
      super(entityType, world);
   }

   @Inject(
      method = {"tryAttack"},
      at = {@At("HEAD")},
      cancellable = true
   )
   public void mialib$customAttacks(@NotNull Entity target, CallbackInfoReturnable<Boolean> cir) {
      if (target.isAttackable()) {
         if (target instanceof EnderDragonPart) {
            target = ((EnderDragonPart)target).owner;
         }

         ItemStack main = this.getMainHandStack();
         if (main.getItem().mialib$attack(this.getWorld(), main, this, target)) {
            cir.setReturnValue(true);
         }
      }
   }

   @WrapOperation(
           method = {"tryAttack"},
           at = {@At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/entity/damage/DamageSources;mobAttack(Lnet/minecraft/entity/LivingEntity;)Lnet/minecraft/entity/damage/DamageSource;"
           )}
   )
   public DamageSource mialib$customDamageType(DamageSources instance, LivingEntity attacker, Operation<DamageSource> original) {
      ItemStack main = this.getMainHandStack();
      if (main.getItem().mialib$setDamageSource(attacker, instance) != null) {
         return main.getItem().mialib$setDamageSource(attacker, instance);
      }

      return original.call(instance, attacker);
   }
}
