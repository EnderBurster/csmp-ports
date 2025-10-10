package aureum.asta.disks.ports.elysium.util;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Optional;

public class PrismPowerLoader {

    private static final Gson GSON = new Gson();

    public static void load(ResourceManager resourceManager) {

        Optional<Resource> resourceOptional = resourceManager.getResource(Elysium.id("attachments/prism_powers.json"));
        if (resourceOptional.isEmpty()) {
            AureumAstaDisks.LOGGER.warn("Prism powers json not found");
            return;
        }

        Resource resource = resourceOptional.get();
        try (InputStream is = resource.getInputStream();
             InputStreamReader reader = new InputStreamReader(is)) {

            JsonObject root = GSON.fromJson(reader, JsonObject.class);
            JsonObject values = root.getAsJsonObject("values");

            for (Map.Entry<String, JsonElement> entry : values.entrySet()) {
                String blockIdStr = entry.getKey();
                int powerValue = entry.getValue().getAsInt();

                Identifier blockId = new Identifier(blockIdStr);
                Block block = Registries.BLOCK.get(blockId);
                if (block == Blocks.AIR) {
                    AureumAstaDisks.LOGGER.warn("Unknown block ID in prism_powers: {}", blockId);
                    continue;
                }

                ElysiumMachines.PRISM_POWERS.put(block, powerValue);
                AureumAstaDisks.LOGGER.info("Loaded prism power {} for block {}", powerValue, blockId);
            }
        } catch (Exception e) {
            AureumAstaDisks.LOGGER.error("Failed to load prism powers JSON", e);
        }
    }
}