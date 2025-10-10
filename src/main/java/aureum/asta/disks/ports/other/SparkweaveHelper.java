package aureum.asta.disks.ports.other;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

public class SparkweaveHelper {

	public static final String MODID = "sparkweave";

	private static final Map<String, ModContainer> MOD_CONTAINERS = new Object2ObjectOpenHashMap<>();

	public static ModContainer getModContainer(String modid) {
		return MOD_CONTAINERS.computeIfAbsent(modid,
				key -> FabricLoader.getInstance().getModContainer(key)
						.orElseThrow(() -> new NoSuchElementException("No mod loaded with ID " + key))
		);
	}

	public static Optional<ModMetadata> tryGetMetadata(String modid) {
		return Optional.ofNullable(MOD_CONTAINERS.computeIfAbsent(modid, key -> FabricLoader.getInstance().getModContainer(key).orElse(null))).map(ModContainer::getMetadata);
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
}
