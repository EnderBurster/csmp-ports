package aureum.asta.disks.init;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.entity.MeteorComponent;
import aureum.asta.disks.cca.entity.ThunderstruckComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class AstaEntityComponents implements EntityComponentInitializer {
    public static final ComponentKey<MeteorComponent> METEOR = ComponentRegistry.getOrCreate(AureumAstaDisks.id("meteor"), MeteorComponent.class);
    public static final ComponentKey<ThunderstruckComponent> THUNDERSTRUCK = ComponentRegistry.getOrCreate(AureumAstaDisks.id("thunderstruck"), ThunderstruckComponent.class);

    @Override
    public void registerEntityComponentFactories(@NotNull EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, METEOR, MeteorComponent::new);
        registry.registerFor(LivingEntity.class, THUNDERSTRUCK, ThunderstruckComponent::new);
    }
}
