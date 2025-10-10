package aureum.asta.disks.ports.mason.init;

import aureum.asta.disks.ports.mason.util.RiderComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

public class MasonComponents implements EntityComponentInitializer {
    public static final ComponentKey<RiderComponent> RIDER_COMPONENT = ComponentRegistry.getOrCreate(new Identifier("aureum-asta-disks", "rider"), RiderComponent.class);

    public MasonComponents() {
    }

    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(RIDER_COMPONENT, RiderComponent::new, RespawnCopyStrategy.NEVER_COPY);
    }
}
