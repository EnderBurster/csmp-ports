package aureum.asta.disks.ports.elysium.util;

import aureum.asta.disks.AureumAstaDisks;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.registry.Registry;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RegistryJsonLoader {

    private static final Gson GSON = new Gson();

    /**
     * Generic loader for JSON registry value maps.
     *
     * @param resourceManager The resource manager to load from
     * @param jsonPath        The path (namespace:path) of the JSON file
     * @param registry        The Minecraft registry (e.g. Registry.BLOCK)
     * @param valueParser     Function to parse JsonElement into value type (e.g. JsonElement::getAsInt)
     * @param onEntry         BiConsumer to receive parsed (registry entry, value)
     * @param <T>             The registry entry type (e.g. Block, Item)
     * @param <V>             The value type (e.g. Integer, Double)
     */
    public static <T, V> void load(
            ResourceManager resourceManager,
            Identifier jsonPath,
            Registry<T> registry,
            Function<JsonElement, V> valueParser,
            BiConsumer<T, V> onEntry
    ) {
        try {
            Optional<Resource> resourceOptional = resourceManager.getResource(jsonPath);
            if (resourceOptional.isEmpty()) {
                AureumAstaDisks.LOGGER.warn("[RegistryJsonLoader] File not found: {}", jsonPath);
                return;
            }

            Resource resource = resourceOptional.get();
            try (InputStream is = resource.getInputStream();
                 InputStreamReader reader = new InputStreamReader(is)) {

                JsonObject root = GSON.fromJson(reader, JsonObject.class);
                JsonObject values = root.getAsJsonObject("values");

                for (String idStr : values.keySet()) {
                    Identifier id = new Identifier(idStr);
                    T entry = registry.get(id);
                    if (entry == null || registry.getRawId(entry) == -1) {
                        AureumAstaDisks.LOGGER.warn("[RegistryJsonLoader] Unknown ID: {}", id);
                        continue;
                    }

                    V value = valueParser.apply(values.get(idStr));
                    onEntry.accept(entry, value);
                }
            }
        } catch (Exception e) {
            AureumAstaDisks.LOGGER.error("[RegistryJsonLoader] Error loading {}:", jsonPath);
        }
    }
}