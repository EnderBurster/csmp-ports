package aureum.asta.disks.mixin.ports.amarite.mialib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.item.Item;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({EnchantmentHelper.class})
public class EnchantmentHelperMixin {
   @WrapOperation(
           method = {"getLevel"},
           at = {@At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevelFromNbt(Lnet/minecraft/nbt/NbtCompound;)I")}
   )
   private static int mialib$enchantAdditions(NbtCompound nbt, Operation<Integer> original, @Local(argsOnly = true) ItemStack stack, @Local(argsOnly = true) Enchantment enchantment) {
      Integer level = original.call(nbt);
      level = stack.getItem().mialib$enchantLevel(enchantment, stack, level);
      return level;
   }

   @WrapOperation(
      method = {"getPossibleEntries"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/enchantment/Enchantment;isAvailableForRandomSelection()Z"
      )}
   )
   private static boolean mialib$storeEnchants(
      Enchantment enchantment, @NotNull Operation<Boolean> original, @Share("storedEnchantment") @NotNull LocalRef<Enchantment> storedEnchantment
   ) {
      storedEnchantment.set(enchantment);
      return (Boolean)original.call(enchantment);
   }

   @WrapOperation(
      method = {"getPossibleEntries"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/enchantment/EnchantmentTarget;isAcceptableItem(Lnet/minecraft/item/Item;)Z"
      )}
   )
   private static boolean mialib$itemAllows(
      EnchantmentTarget enchantmentTarget,
      @NotNull Item item,
      Operation<Boolean> original,
      @Share("storedEnchantment") @NotNull LocalRef<Enchantment> storedEnchantment
   ) {
      Enchantment stored = (Enchantment)storedEnchantment.get();
      ActionResult result = item.mialib$checkEnchantment(enchantmentTarget, stored);
      return result != null && result != ActionResult.PASS
         ? result == ActionResult.SUCCESS
         : (Boolean)original.call(enchantmentTarget, item);
   }
}
