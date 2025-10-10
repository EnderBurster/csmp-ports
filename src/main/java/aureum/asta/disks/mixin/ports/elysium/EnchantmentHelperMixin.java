package aureum.asta.disks.mixin.ports.elysium;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import aureum.asta.disks.ports.elysium.CustomEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({EnchantmentHelper.class})
public class EnchantmentHelperMixin {
   @WrapOperation(
      method = {"getPossibleEntries"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"
      )}
   )
   private static boolean elysium$allowCustomEnchantingRules(
      EnchantmentTarget category, Item item, Operation<Boolean> original, int level, ItemStack stack, boolean allowTreasure, @Local Enchantment enchantment
   ) {
      return enchantment instanceof CustomEnchantment custom ? custom.customCanEnchant(stack) : (Boolean)original.call(category, item);
   }
}
