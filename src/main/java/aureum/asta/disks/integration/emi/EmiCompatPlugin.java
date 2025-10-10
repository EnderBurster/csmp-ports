package aureum.asta.disks.integration.emi;

import aureum.asta.disks.integration.emi.farmersdelight.FarmersDelightPlugin;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class EmiCompatPlugin implements EmiPlugin {
    public static final Logger LOGGER = LoggerFactory.getLogger("emicompat");

    @Override
    public void register(EmiRegistry emi) {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("farmersdelight"))
            new FarmersDelightPlugin().register(emi);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Recipe<?>> void addAll(EmiRegistry registry, RecipeType type, Function<T, EmiRecipe> constructor) {
        for (T recipe : (List<T>) registry.getRecipeManager().listAllOfType(type)) {
            registry.addRecipe(constructor.apply(recipe));
        }
    }
}
