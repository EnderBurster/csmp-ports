package aureum.asta.disks.mixin.ports.amarite.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.util.ActionResult;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({EnchantRandomlyLootFunction.class})
public class EnchantRandomlyLootFunctionMixin {
   @WrapOperation(
      method = {"method_26267"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/enchantment/Enchantment;isAcceptableItem(Lnet/minecraft/item/ItemStack;)Z"
      )}
   )
   private static boolean mialib$itemAllows(Enchantment enchantment, @NotNull ItemStack stack, Operation<Boolean> original) {
      ActionResult result = stack.getItem().mialib$checkEnchantment(enchantment.target, enchantment);
      return result != null && result != ActionResult.PASS ? result == ActionResult.SUCCESS : (Boolean)original.call(enchantment, stack);
   }
}
