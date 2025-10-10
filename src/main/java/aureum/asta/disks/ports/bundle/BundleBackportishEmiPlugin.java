package aureum.asta.disks.ports.bundle;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiCraftingRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;

import java.util.List;

public class BundleBackportishEmiPlugin implements EmiPlugin {
    private static final List<Item> BUNDLES = Registries.ITEM.stream().filter(item -> item instanceof BundleItem).toList();

    @Override
    public void register(EmiRegistry emiRegistry) {
        if (emiRegistry.getRecipeManager().listAllOfType(RecipeType.CRAFTING).stream()
                .anyMatch(craftingRecipe -> craftingRecipe instanceof BundleColoringRecipe)) {
            BundleColoringRecipe.COLORS.forEach((dye, bundle) -> {
                emiRegistry.addRecipe(new EmiCraftingRecipe(
                        List.of(EmiIngredient.of(BUNDLES.stream()
                                .filter(item -> item != bundle)
                                .map(EmiStack::of)
                                .toList()), EmiStack.of(dye)),
                        EmiStack.of(bundle),
                        Registries.ITEM.getId(bundle).withPrefixedPath("/"),
                        true));
            });
        }
    }
}
