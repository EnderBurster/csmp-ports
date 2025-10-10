package aureum.asta.disks.ports.charter.common.component;

import aureum.asta.disks.ports.charter.Charter;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;

public class CharterComponents implements EntityComponentInitializer, WorldComponentInitializer {
   public static final ComponentKey<CharterPlayerComponent> PLAYER_COMPONENT = ComponentRegistry.getOrCreate(Charter.id("player"), CharterPlayerComponent.class);
   public static final ComponentKey<CharterArmComponent> ARM_COMPONENT = ComponentRegistry.getOrCreate(Charter.id("arm"), CharterArmComponent.class);
   public static final ComponentKey<CharterWorldComponent> CHARTER = ComponentRegistry.getOrCreate(Charter.id("charter"), CharterWorldComponent.class);

   public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
      registry.registerForPlayers(PLAYER_COMPONENT, CharterPlayerComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
      registry.registerForPlayers(ARM_COMPONENT, CharterArmComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
   }

   public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
      registry.register(CHARTER, CharterWorldComponent::new);
   }
}
