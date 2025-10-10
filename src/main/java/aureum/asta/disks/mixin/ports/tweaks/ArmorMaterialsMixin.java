package aureum.asta.disks.mixin.ports.tweaks;

import net.minecraft.item.ArmorMaterials;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ArmorMaterials.class})
public class ArmorMaterialsMixin {
    @Shadow
    @Final
    private String name;

    @Inject(
            method = {"getRepairIngredient"},
            at = {@At("TAIL")},
            cancellable = true
    )
    public void getRepairIngredient(CallbackInfoReturnable<Ingredient> cir) {
        if (this.name.equals("netherite")) {
            cir.setReturnValue(Ingredient.ofItems(new ItemConvertible[]{Items.DIAMOND}));
        }
    }
}
