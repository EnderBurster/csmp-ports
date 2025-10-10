package aureum.asta.disks.mixin.ports.cursepatch;

import moriyashiine.enchancement.common.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {ItemStack.class},
   priority = 999
)
public class ItemStackMixin {
   @Unique
   private static boolean toggle = false;

   @Inject(
      method = {"addEnchantment"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cursepatch$lol(Enchantment enchantment, int level, CallbackInfo ci) {
      if (ModConfig.enchantmentLimit == 1) {
         ModConfig.enchantmentLimit = 2;
         boolean foundNormal = false;

         for (Enchantment foundEnchantment : EnchantmentHelper.get(ItemStack.class.cast(this)).keySet()) {
            if (!foundEnchantment.isCursed()) {
               if (foundNormal) {
                  ci.cancel();
                  break;
               }

               foundNormal = true;
            }
         }

         toggle = true;
      }
   }

   @Inject(
      method = {"addEnchantment"},
      at = {@At("TAIL")}
   )
   private void cursepatch$lolTail(Enchantment enchantment, int level, CallbackInfo ci) {
      if (toggle) {
         ModConfig.enchantmentLimit = 1;
         toggle = false;
      }
   }
}
