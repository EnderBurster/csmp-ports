package aureum.asta.disks.mixin.ports.cursepatch;

import moriyashiine.enchancement.common.ModConfig;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(
   value = {Enchantment.class},
   priority = 999
)
public abstract class EnchantmentMixin {
   @Shadow
   public abstract boolean isCursed();

   @Inject(
      method = {"canCombine"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cursepatch$ilyratbb(Enchantment other, CallbackInfoReturnable<Boolean> cir) {
      if (ModConfig.enchantmentLimit == 1 && (this.isCursed() || other.isCursed())) {
         cir.setReturnValue(true);
      }
   }
}
