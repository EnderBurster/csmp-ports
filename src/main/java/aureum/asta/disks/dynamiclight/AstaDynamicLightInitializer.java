package aureum.asta.disks.dynamiclight;

import aureum.asta.disks.init.AstaEntities;
import aureum.asta.disks.ports.charter.common.init.CharterEntities;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandler;
import dev.lambdaurora.lambdynlights.api.DynamicLightHandlers;
import dev.lambdaurora.lambdynlights.api.DynamicLightsInitializer;

public class AstaDynamicLightInitializer implements DynamicLightsInitializer {
    @Override
    public void onInitializeDynamicLights() {
        DynamicLightHandlers.registerDynamicLightHandler(AstaEntities.GRIMOIRE_SHARK, DynamicLightHandler.makeHandler(sharkEntity -> 15, sharkEntity -> false));
    }
}
