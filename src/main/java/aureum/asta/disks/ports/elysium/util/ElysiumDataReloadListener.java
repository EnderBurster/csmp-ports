package aureum.asta.disks.ports.elysium.util;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ElysiumDataReloadListener implements SimpleSynchronousResourceReloadListener {

    @Override
    public Identifier getFabricId() {
        return Elysium.id("data_reload_listener");
    }

    @Override
    public void reload(ResourceManager manager) {
        System.out.println("[Elysium] Reloading data...");

        // Clear old data
        ElysiumMachines.PRISM_POWERS.clear();
        ElysiumMachines.ITEM_CONDUCTIVITY.clear();
        ElysiumMachines.ENTITY_CONDUCTIVITY.clear();
        ElysiumMachines.ITEM_MAGNETISM.clear();
        ElysiumMachines.ENTITY_MAGNETISM.clear();

        // Reload JSONs
        RegistryJsonLoader.load(
                manager,
                Elysium.id("attachments/block/prism_powers.json"),
                Registries.BLOCK,
                JsonElement::getAsInt,
                ElysiumMachines.PRISM_POWERS::put
        );

        RegistryJsonLoader.load(
                manager,
                Elysium.id("attachments/item/conductivity.json"),
                Registries.ITEM,
                JsonElement::getAsDouble,
                ElysiumMachines.ITEM_CONDUCTIVITY::put
        );

        RegistryJsonLoader.load(
                manager,
                Elysium.id("attachments/entity_type/conductivity.json"),
                Registries.ENTITY_TYPE,
                JsonElement::getAsDouble,
                ElysiumMachines.ENTITY_CONDUCTIVITY::put
        );

        RegistryJsonLoader.load(
                manager,
                Elysium.id("attachments/item/magnetism.json"),
                Registries.ITEM,
                JsonElement::getAsDouble,
                ElysiumMachines.ITEM_MAGNETISM::put
        );

        RegistryJsonLoader.load(
                manager,
                Elysium.id("attachments/entity_type/magnetism.json"),
                Registries.ENTITY_TYPE,
                JsonElement::getAsDouble,
                ElysiumMachines.ENTITY_MAGNETISM::put
        );

        System.out.println("[Elysium] Data reload complete.");
    }
}