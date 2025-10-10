package aureum.asta.disks.mixin.ports.amarite.amarite.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraft.sound.SoundCategory;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.BuddedComponent;
import aureum.asta.disks.ports.amarite.amarite.cca.DiscComponent;
import aureum.asta.disks.ports.amarite.amarite.cca.LongswordAccumulateComponent;
import aureum.asta.disks.ports.amarite.amarite.cca.LongswordDashComponent;
import aureum.asta.disks.ports.amarite.amarite.cca.LongswordDoubleDashComponent;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;

@Mixin({PlayerEntity.class})
public abstract class PlayerEntityMixin extends LivingEntity {
   @Shadow public abstract void disableShield(boolean sprinting);

   protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
      super(entityType, world);
   }

   @Inject(
      method = {"damage"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void amarite$noDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
      if (((LongswordDashComponent)Amarite.DASH.get(this)).isDashing() || ((LongswordDoubleDashComponent)Amarite.DOUBLE_DASH.get(this)).isDashing()) {
         cir.setReturnValue(false);
      }

      ((DiscComponent)Amarite.DISC.get(this)).chargeOrbit(amount);
   }

   @WrapOperation(
      method = {"applyDamage"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/player/PlayerEntity;modifyAppliedDamage(Lnet/minecraft/entity/damage/DamageSource;F)F"
      )}
   )
   private float amarite$swordBlock(PlayerEntity player, DamageSource source, float amount, @NotNull Operation<Float> original) {
      float base = (Float)original.call(player, source, amount);
      ItemStack stack = player.getMainHandStack();
      if (!this.world.isClient()
         && stack.isOf(AmariteItems.AMARITE_LONGSWORD)
         && player.mialib$isUsing()
         && !((LongswordAccumulateComponent)Amarite.ACCUMULATE.get(player)).accumulateActive) {
         Vec3d damagePos = source.getPosition();
         if (damagePos != null) {
            Vec3d rotVec = this.getRotationVec(1.0F);
            Vec3d difference = damagePos.relativize(this.getEyePos()).normalize();
            double angle = difference.dotProduct(rotVec);
            if (!(angle < -1.0) && angle < -0.35) {
               this.world
                  .playSoundFromEntity(
                     null, this, AmariteSoundEvents.SWORD_BLOCK, SoundCategory.HOSTILE, 1.0F, 1.0F + this.world.random.nextFloat() * 0.4F
                  );
               AmariteLongswordItem.getMode(player, stack).absorbDamage(base);
               return base / 2.0F;
            }
         } else if (source.isOf(DamageTypes.FALL)) {
            Vec3d rotVec = this.getRotationVec(1.0F);
            Vec3d difference = new Vec3d(0.0, 1.0, 0.0);
            double angle = difference.dotProduct(rotVec);
            if (angle < -0.35) {
               this.world
                  .playSoundFromEntity(
                     null, this, AmariteSoundEvents.SWORD_BLOCK, SoundCategory.HOSTILE, 1.0F, 1.0F + this.world.random.nextFloat() * 0.4F
                  );
               AmariteLongswordItem.getMode(player, stack).absorbDamage(base / 2.0F);
               return base / 4.0F;
            }
         }
      }

      return base;
   }

   @Inject(
      method = {"eatFood"},
      at = {@At("HEAD")}
   )
   private void amarite$eatedItAll(World world, @NotNull ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
      BuddedComponent budding = (BuddedComponent)Amarite.BUDDED.get(this);
      if (stack.getNbt() != null) {
         if (stack.getNbt().getBoolean("Budded")) {
            budding.setBudTime(12000);
         }

         if (stack.getNbt().getBoolean("Curative")) {
            budding.setBudTime(0);
         }
      }

      if (stack.isOf(Items.HONEY_BOTTLE)) {
         budding.setBudTime(0);
      }
   }

   @Inject(
      method = {"attack"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/player/PlayerEntity;onAttacking(Lnet/minecraft/entity/Entity;)V"
      )}
   )
   private void amarite$chargeRebound(Entity target, CallbackInfo ci) {
      ((DiscComponent)Amarite.DISC.get(this)).chargeRebound(1);
   }
}
