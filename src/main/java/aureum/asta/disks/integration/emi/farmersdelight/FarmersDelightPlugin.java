package aureum.asta.disks.integration.emi.farmersdelight;

import aureum.asta.disks.integration.emi.farmersdelight.recipe.EmiCookingPotRecipe;
import aureum.asta.disks.integration.emi.farmersdelight.recipe.EmiCuttingBoardRecipe;
import aureum.asta.disks.integration.emi.farmersdelight.recipe.EmiDecompositionRecipe;
import com.nhoryzon.mc.farmersdelight.recipe.CookingPotRecipe;
import com.nhoryzon.mc.farmersdelight.recipe.CuttingBoardRecipe;
import com.nhoryzon.mc.farmersdelight.registry.BlocksRegistry;
import com.nhoryzon.mc.farmersdelight.registry.ItemsRegistry;
import com.nhoryzon.mc.farmersdelight.registry.RecipeTypesRegistry;
import com.nhoryzon.mc.farmersdelight.registry.TagsRegistry;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.nhoryzon.mc.farmersdelight.FarmersDelightMod.MOD_ID;
import static aureum.asta.disks.integration.emi.EmiCompatPlugin.addAll;

public class FarmersDelightPlugin implements EmiPlugin {
    public static final Map<Identifier, EmiRecipeCategory> ALL = new LinkedHashMap<>();
    public static final EmiRecipeCategory
            COOKING = register("cooking", EmiStack.of(ItemsRegistry.COOKING_POT.get())),
            CUTTING = register("cutting", EmiStack.of(ItemsRegistry.CUTTING_BOARD.get())),
            DECOMPOSITION = register("decomposition", EmiStack.of(ItemsRegistry.RICH_SOIL.get()));


    @Override
    public void register(EmiRegistry registry) {
        RecipeType<CookingPotRecipe> COOKING_T = RecipeTypesRegistry.COOKING_RECIPE_SERIALIZER.type();
        RecipeType<CuttingBoardRecipe> CUTTING_T = RecipeTypesRegistry.CUTTING_RECIPE_SERIALIZER.type();

        ALL.forEach((id, category) -> registry.addCategory(category));

        registry.addWorkstation(VanillaEmiRecipeCategories.CAMPFIRE_COOKING, EmiStack.of(ItemsRegistry.STOVE.get()));
        registry.addWorkstation(VanillaEmiRecipeCategories.CAMPFIRE_COOKING, EmiStack.of(ItemsRegistry.SKILLET.get()));

        registry.addWorkstation(FarmersDelightPlugin.COOKING, EmiStack.of(ItemsRegistry.COOKING_POT.get()));
        addAll(registry, COOKING_T, EmiCookingPotRecipe::new);

        registry.addWorkstation(CUTTING, EmiStack.of(ItemsRegistry.CUTTING_BOARD.get()));
        addAll(registry, CUTTING_T, EmiCuttingBoardRecipe::new);

        registry.addRecipe(new EmiDecompositionRecipe(
                List.of(EmiIngredient.of(Ingredient.ofItems(BlocksRegistry.ORGANIC_COMPOST.get()))),
                List.of(EmiStack.of(BlocksRegistry.RICH_SOIL.get())),
                Registries.BLOCK.getEntryList(TagsRegistry.COMPOST_ACTIVATORS).stream()
                        .parallel()
                        .flatMap(RegistryEntryList::stream)
                        .map(RegistryEntry::value)
                        .toList()
        ));
    }

    private static EmiRecipeCategory register(String name, EmiRenderable icon) {
        Identifier id = new Identifier(MOD_ID, name);
        EmiRecipeCategory category = new EmiRecipeCategory(id, icon);
        ALL.put(id, category);
        return category;
    }
}
