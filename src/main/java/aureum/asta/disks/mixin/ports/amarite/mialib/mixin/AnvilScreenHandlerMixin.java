package aureum.asta.disks.mixin.ports.amarite.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.util.ActionResult;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({AnvilScreenHandler.class})
public class AnvilScreenHandlerMixin {
   @WrapOperation(
      method = {"updateResult"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"
      )}
   )
   private boolean mialib$itemAllows(Enchantment enchantment, @NotNull ItemStack stack, Operation<Boolean> original) {
      ActionResult result = stack.getItem().mialib$checkEnchantment(enchantment.target, enchantment);
      return result != null && result != ActionResult.PASS ? result == ActionResult.SUCCESS : (Boolean)original.call(enchantment, stack);
   }
}
