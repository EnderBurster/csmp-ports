package aureum.asta.disks.cca;

import aureum.asta.disks.AureumAstaDisks;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.item.ItemComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.item.ItemComponentInitializer;
import net.minecraft.entity.player.PlayerEntity;

public class ArsenalComponents implements EntityComponentInitializer, ItemComponentInitializer {
    public static final ComponentKey<BackWeaponComponent> BACK_WEAPON_COMPONENT = ComponentRegistry.getOrCreate(
            AureumAstaDisks.id("back_weapon"), BackWeaponComponent.class
    );

    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.beginRegistration(PlayerEntity.class, BACK_WEAPON_COMPONENT).respawnStrategy(RespawnCopyStrategy.CHARACTER).end(BackWeaponComponent::new);
    }

    @Override
    public void registerItemComponentFactories(ItemComponentFactoryRegistry itemComponentFactoryRegistry) {

    }
}
