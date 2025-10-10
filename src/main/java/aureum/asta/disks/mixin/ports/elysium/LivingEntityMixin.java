package aureum.asta.disks.mixin.ports.elysium;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.ElysiumDamageSources;
import aureum.asta.disks.ports.elysium.armour.ElysiumArmour;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({LivingEntity.class})
public abstract class LivingEntityMixin extends Entity {
   @Shadow
   protected ItemStack activeItemStack;

   public LivingEntityMixin(EntityType<?> entityType, World level) {
      super(entityType, level);
   }

   @Shadow
   @Nullable
   public abstract StatusEffectInstance getStatusEffect(StatusEffect var1);

   @Shadow
   public abstract boolean hasStatusEffect(StatusEffect var1);

   @Shadow
   public abstract boolean removeStatusEffect(StatusEffect var1);

   @Shadow public abstract ItemStack getMainHandStack();

   @ModifyVariable(
      method = {"damage"},
      at = @At("HEAD"),
      argsOnly = true
   )
   private float elysium$modifyDamageForVulnerability(float amount, DamageSource source) {
      StatusEffectInstance effect = this.getStatusEffect(ElysiumArmour.ELYSIUM_VULNERABILITY);
      return effect != null && ElysiumArmour.isAffectedByElysiumVulnerability(source) ? amount + amount * 0.05F * (float)(effect.getAmplifier() + 2) : amount;
   }

   @Inject(
      method = {"tickStatusEffects"},
      at = {@At("HEAD")}
   )
   private void elysium$cancelVulnerability(CallbackInfo ci) {
      if (this.hasStatusEffect(ElysiumArmour.ELYSIUM_VULNERABILITY) && this.isInsideWaterOrBubbleColumn()) {
         this.removeStatusEffect(ElysiumArmour.ELYSIUM_VULNERABILITY);
         this.damage(world.getDamageSources().create(ElysiumDamageSources.VULNERABILITY_WASH_AWAY), 1.0F);
      }
   }

   @Inject(
      method = {"getHandSwingProgress"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void elysium$dontSwingArmForCheirosiphon(float partialTick, CallbackInfoReturnable<Float> cir) {
      if (this.getMainHandStack().isOf(Elysium.CHEIROSIPHON)) {
         cir.setReturnValue(0.0F);
      }
   }
}
