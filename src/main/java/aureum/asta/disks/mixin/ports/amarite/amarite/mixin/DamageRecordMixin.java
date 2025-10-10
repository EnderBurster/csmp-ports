package aureum.asta.disks.mixin.ports.amarite.amarite.mixin;

import aureum.asta.disks.ports.amarite.amarite.Amarite;
import net.minecraft.entity.damage.DamageRecord;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import aureum.asta.disks.ports.amarite.amarite.items.MaskItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEnchantments;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DamageRecord.class})
public class DamageRecordMixin {
   @Inject(
           method = "getAttackerName",
           at = @At("HEAD"),
           cancellable = true
   )
   private void asta$removeName(CallbackInfoReturnable<Text> cir)
   {
      DamageRecord record = (DamageRecord)(Object)this;
      if (record.getDamageSource().getAttacker() instanceof LivingEntity living) {
         ItemStack mask = MaskItem.getWornMask(living);
         if (EnchantmentHelper.getLevel(AmariteEnchantments.ANONYMITY, mask) > 0) {
            cir.setReturnValue(Text.literal("Somebody"));
            cir.cancel();
         }
      }
   }

   /*@WrapOperation(
      method = {"getAttackerName"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"
      )}
   )
   private Text nameless$removeName(Entity instance, Operation<Text> original) {
      if (instance instanceof LivingEntity living) {
         ItemStack mask = MaskItem.getWornMask(living);
         if (EnchantmentHelper.getLevel(AmariteEnchantments.ANONYMITY, mask) > 0) {
            Amarite.LOGGER.info("Has Anonymity: {} {}", EnchantmentHelper.getLevel(AmariteEnchantments.ANONYMITY, mask), mask);
            return Text.literal("Somebody");
         }
      }

      return (Text)original.call(instance);
   }*/
}
