package aureum.asta.disks.mixin.ports.mace;

import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.PlayerEntityMaceInterface;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Debug(
        export = true
)
@Mixin({Explosion.class})
public abstract class ExplosionLoggingMixin implements PlayerEntityMaceInterface {
   @Shadow
   @Final
   private double x;
   @Shadow
   @Final
   private double y;
   @Shadow
   @Final
   private double z;
   @Shadow
   @Final
   private ExplosionBehavior behavior;

   public ExplosionLoggingMixin() {
   }

   @WrapOperation(
           at = {@At(
                   value = "INVOKE",
                   target = "Ljava/lang/Math;sqrt(D)D",
                   ordinal = 1
           )},
           method = {"collectBlocksAndDamageEntities"}
   )
   private double logggy(double a, Operation<Double> original, @Local Entity entity) {
      double o = (Double)original.call(a);
      double e = entity.getX() - this.x;
      double g = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
      double h = entity.getZ() - this.z;
      if (entity instanceof ServerPlayerEntity && FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.info("entity {} with pos {} (DIFF: {} {} {}) affected by explosion at {} {} {} giving result {}", new Object[]{entity, entity.getPos(), e, g, h, this.x, this.y, this.z, o});
      }

      return o;
   }

   @ModifyVariable(
           method = {"collectBlocksAndDamageEntities"},
           at = @At("STORE"),
           ordinal = 1
   )
   private Vec3d finalVelChangeLog(Vec3d vec3d2, @Local Entity entity) {
      if (FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.error("final vel change for entity {} is: INTERCEPT SUCCESS {}", entity, vec3d2);
      }

      return vec3d2;
   }

   /*@ModifyVariable(
           method = {"collectBlocksAndDamageEntities"},
           at = @At("STORE"),
           ordinal = 4
   )
   private double lookingForAA(double aa, @Local Entity entity) {
      if (entity instanceof ServerPlayerEntity && FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.error("looking for AA in entity {} with val {}", entity, aa);
      }

      return aa;
   }

   @ModifyVariable(
           method = {"collectBlocksAndDamageEntities"},
           at = @At("STORE"),
           ordinal = 5
   )
   private double lookingForExposure(double aa, @Local Entity entity) {
      if (entity instanceof ServerPlayerEntity && FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.error("looking for Exposure in entity {} with val {}", entity, aa);
      }

      return aa;
   }

   @ModifyVariable(
           method = {"collectBlocksAndDamageEntities"},
           at = @At("STORE"),
           ordinal = 6
   )
   private double lookingForAC(double ac, @Local Entity entity) {
      if (entity instanceof ServerPlayerEntity && FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.error("looking for AC in entity {} with val {}", entity, ac);
      }

      if (this.behavior instanceof WindChargeNoDamageEntitiesExplosionBehavior) {
         return entity instanceof WindChargeEntity ? ac : ac * (double)((WindChargeNoDamageEntitiesExplosionBehavior)this.behavior).knockbackMultiplier;
      } else {
         return ac;
      }
   }*/

   @WrapOperation(
           method = {"collectBlocksAndDamageEntities"},
           at = {@At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/enchantment/ProtectionEnchantment;transformExplosionKnockback(Lnet/minecraft/entity/LivingEntity;D)D"
           )}
   )
   private double ontransknockback(LivingEntity entity, double velocity, Operation<Double> original) {
      double result = (Double)original.call(entity, velocity);
      if (entity instanceof ServerPlayerEntity && FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.error("prot changing knockback from {} to {}", velocity, result);
      }

      return result;
   }

   @WrapOperation(
           method = {"collectBlocksAndDamageEntities"},
           at = {@At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/entity/Entity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V"
           )}
   )
   private void logggy2(Entity entity, Vec3d velocity, Operation<Void> original) {
      Vec3d old = new Vec3d(entity.getVelocity().x, entity.getVelocity().y, entity.getVelocity().z);
      float MORE = 0.0F;
      if (entity instanceof ServerPlayerEntity && FaithfulMace.superfluousLogging()) {
         FaithfulMace.MOGGER.info("using knockback of {} on vel {} for entity {}", new Object[]{velocity.subtract(old).add((double)0.0F, (double)MORE, (double)0.0F), old, entity});
      }

      original.call(entity, velocity.add((double)0.0F, (double)MORE, (double)0.0F));
   }
}
