package aureum.asta.disks.mixin.ports.amarite.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.mialib.MiaLib;
import aureum.asta.disks.ports.amarite.mialib.cca.IdCooldownComponent;
import aureum.asta.disks.ports.amarite.mialib.interfaces.MPlayerEntity;

@Mixin({PlayerEntity.class})
public abstract class PlayerEntityMixin extends LivingEntity implements MPlayerEntity {
   @Shadow @Final private PlayerInventory inventory;

   protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
      super(entityType, world);
   }

   @Inject(
      method = {"attack"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/player/PlayerEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"
      )},
      cancellable = true
   )
   public void mialib$customAttacks(@NotNull Entity target, CallbackInfo ci) {
      if (target.isAttackable()) {
         if (target instanceof EnderDragonPart) {
            target = ((EnderDragonPart)target).owner;
         }

         ItemStack main = this.getMainHandStack();
         if (main.getItem().mialib$attack(this.getWorld(), main, this, target)) {
            ci.cancel();
         }
      }
   }

   @WrapOperation(
           method = {"attack"},
           at = {@At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/entity/damage/DamageSources;playerAttack(Lnet/minecraft/entity/player/PlayerEntity;)Lnet/minecraft/entity/damage/DamageSource;"
           )}
   )
   public DamageSource mialib$customDamageType(DamageSources instance, PlayerEntity attacker, Operation<DamageSource> original) {
      ItemStack main = this.getMainHandStack();
      if (main.getItem().mialib$setDamageSource(attacker, instance) != null) {
         return main.getItem().mialib$setDamageSource(attacker, instance);
      }

      return original.call(instance, attacker);
   }

   @Override
   public boolean mialib$isCoolingDown(Identifier id) {
      return ((IdCooldownComponent)MiaLib.ID_COOLDOWN_COMPONENT.get(this)).isCoolingDown(id);
   }

   @Override
   public void mialib$setCooldown(Identifier id, int ticks) {
      ((IdCooldownComponent)MiaLib.ID_COOLDOWN_COMPONENT.get(this)).setCooldown(id, ticks);
   }

   @Override
   public int mialib$getCooldown(Identifier id) {
      return ((IdCooldownComponent)MiaLib.ID_COOLDOWN_COMPONENT.get(this)).getCooldown(id);
   }

   @Override
   public float mialib$getCooldown(Identifier id, float tickDelta) {
      return ((IdCooldownComponent)MiaLib.ID_COOLDOWN_COMPONENT.get(this)).getCooldown(id, tickDelta);
   }
}
