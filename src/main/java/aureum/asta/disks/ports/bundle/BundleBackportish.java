package aureum.asta.disks.ports.bundle;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleBackportish implements ModInitializer {
	public static final String MOD_ID = "aureum-asta-disks";

	public static Identifier id(String path) {
		return Identifier.of(MOD_ID, path);
	}
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final RecipeSerializer<BundleColoringRecipe> COLORING_RECIPE = Registry.register(
			Registries.RECIPE_SERIALIZER,
			id("crafting_special_bundlecoloring"),
			new SpecialRecipeSerializer<>(BundleColoringRecipe::new)
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		BundleBackportishItems.register();

		BundleBackportishNetworking.register();

        //noinspection OptionalGetWithoutIsPresent
        ResourceManagerHelper.registerBuiltinResourcePack(
				id("rabbithide"),
				FabricLoader.getInstance().getModContainer(MOD_ID).get(),
				Text.of("Rabbit Hide Bundle"),
				ResourcePackActivationType.NORMAL
		);
	}
}
