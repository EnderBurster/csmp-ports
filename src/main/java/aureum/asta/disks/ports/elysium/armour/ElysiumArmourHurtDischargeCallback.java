package aureum.asta.disks.ports.elysium.armour;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface ElysiumArmourHurtDischargeCallback {
    Event<ElysiumArmourHurtDischargeCallback> EVENT = EventFactory.createArrayBacked(
            ElysiumArmourHurtDischargeCallback.class,
            (callbacks) -> (armour, wearer, target) -> {
                for (ElysiumArmourHurtDischargeCallback callback : callbacks) {
                    if (callback.handleHurtDischarge(armour, wearer, target)) {
                        return true;
                    }
                }
                return false;
            }
    );

   boolean handleHurtDischarge(ElysiumArmourComponent var1, LivingEntity var2, @Nullable Entity var3);
}
