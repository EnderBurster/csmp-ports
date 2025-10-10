package aureum.asta.disks.ports.impaled.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.function.Predicate;

public interface ICustomTrackingItem {
    Predicate<Entity> mialeeMisc$getTrackingPredicate(PlayerEntity user);
}