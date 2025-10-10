package aureum.asta.disks.ports.elysium.util;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class PrismPowerReloadListener implements SimpleSynchronousResourceReloadListener {
    private static final Identifier ID = Elysium.id("prism_powers_loader");

    @Override
    public Identifier getFabricId() {
        return ID;
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        ElysiumMachines.PRISM_POWERS.clear();
        PrismPowerLoader.load(resourceManager);
    }
}