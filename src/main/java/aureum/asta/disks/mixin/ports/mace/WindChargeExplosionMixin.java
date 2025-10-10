package aureum.asta.disks.mixin.ports.mace;

import aureum.asta.disks.ports.mace.PlayerEntityMaceInterface;
import aureum.asta.disks.ports.mace.entity.WindChargeNoDamageEntitiesExplosionBehavior;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({Explosion.class})
public abstract class WindChargeExplosionMixin implements PlayerEntityMaceInterface {
   @Shadow
   @Final
   private ExplosionBehavior behavior;

   public WindChargeExplosionMixin() {
   }

   @WrapOperation(
           method = {"collectBlocksAndDamageEntities"},
           at = {@At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
           )}
   )
   private boolean useWindChargeExplosionDamageBehavior(Entity instance, DamageSource source, float amount, Operation<Boolean> original) {
      return this.behavior instanceof WindChargeNoDamageEntitiesExplosionBehavior ? false : (Boolean)original.call(instance, source, amount);
   }
}