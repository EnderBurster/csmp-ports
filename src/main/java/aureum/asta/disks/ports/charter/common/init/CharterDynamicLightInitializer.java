package aureum.asta.disks.ports.charter.common.init;

import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;
import net.minecraft.entity.EntityType;

public class CharterDynamicLightInitializer implements DynamicLightsInitializer {
    @Override
    public void onInitializeDynamicLights() {
        DynamicLightHandlers.registerDynamicLightHandler(CharterEntities.CHAINS, (entity) -> 10);
        DynamicLightHandlers.registerDynamicLightHandler(CharterEntities.EPITAPH_CHAINS, (entity) -> 10);
        DynamicLightHandlers.registerDynamicLightHandler(CharterEntities.LESSER_DIVINITY_ENTITY, (entity) -> 15);
    }
}