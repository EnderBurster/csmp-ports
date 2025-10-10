package aureum.asta.disks.mixin.ports.amarite.amarite.mixin;

import aureum.asta.disks.ports.amarite.amarite.Amarite;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import aureum.asta.disks.ports.amarite.amarite.items.MaskItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEnchantments;

@Mixin(DamageSource.class)
public class DamageSourceMixin {
   @WrapOperation(
      method = {"getDeathMessage"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/entity/Entity;getDisplayName()Lnet/minecraft/text/Text;"
      )}
   )
   private Text nameless$removeName(Entity instance, Operation<Text> original) {
      if (instance instanceof LivingEntity entity)
      {
         ItemStack mask = MaskItem.getWornMask(entity);
         return (Text)(EnchantmentHelper.getLevel(AmariteEnchantments.ANONYMITY, mask) > 0 ? Text.literal("Somebody") : (Text)original.call(instance));
      }

      return (Text)original.call(instance);
   }

   @WrapOperation(
           method = {"getDeathMessage"},
           at = {@At(
                   value = "INVOKE",
                   target = "Lnet/minecraft/entity/LivingEntity;getDisplayName()Lnet/minecraft/text/Text;"
           )}
   )
   private Text nameless$removeName(LivingEntity instance, Operation<Text> original) {
      ItemStack mask = MaskItem.getWornMask(instance);
      return (Text)(EnchantmentHelper.getLevel(AmariteEnchantments.ANONYMITY, mask) > 0 ? Text.literal("Somebody") : (Text)original.call(instance));
   }
}
