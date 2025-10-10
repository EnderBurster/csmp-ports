package aureum.asta.disks.ports.charter.common.recipe;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CharterRecipes {
    public static final RecipeSerializer<UuidLockedRecipe> UUID_LOCKED_SHAPED_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, new Identifier("charter", "uuid_locked_shaped"), new UuidLockedShapedRecipeSerializer());

    public static void init() {}
}
