package aureum.asta.disks.mixin.ports.tweaks;

import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ToolMaterials.class})
public class ToolMaterialsMixin {
   @Shadow
   @Final
   private int miningLevel;

   @Inject(
      method = {"getRepairIngredient"},
      at = {@At("TAIL")},
      cancellable = true
   )
   public void getRepairIngredient(CallbackInfoReturnable<Ingredient> cir) {
      if (this.miningLevel == 4) {
         cir.setReturnValue(Ingredient.ofItems(new ItemConvertible[]{Items.DIAMOND}));
      }
   }
}
