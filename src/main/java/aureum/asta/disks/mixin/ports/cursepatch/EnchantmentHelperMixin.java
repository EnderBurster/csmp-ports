package aureum.asta.disks.mixin.ports.cursepatch;

import java.util.Map;
import moriyashiine.enchancement.common.ModConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(
   value = {EnchantmentHelper.class},
   priority = 999
)
public abstract class EnchantmentHelperMixin {
   @Unique
   private static boolean toggle = false;

   @Shadow
   public static Map<Enchantment, Integer> get(ItemStack stack) {
      throw new UnsupportedOperationException();
   }

   @Inject(
      method = {"set"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private static void cursepatch$funny(Map<Enchantment, Integer> enchantments, ItemStack stack, CallbackInfo ci) {
      if (ModConfig.enchantmentLimit == 1) {
         ModConfig.enchantmentLimit = 2;
         boolean foundNormal = false;

         for (Enchantment enchantment : get(stack).keySet()) {
            if (!enchantment.isCursed()) {
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
      method = {"set"},
      at = {@At("TAIL")}
   )
   private static void cursepatch$funnyTail(Map<Enchantment, Integer> enchantments, ItemStack stack, CallbackInfo ci) {
      if (toggle) {
         ModConfig.enchantmentLimit = 1;
         toggle = false;
      }
   }
}
