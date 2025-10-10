package aureum.asta.disks.ports.elysium.armour;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface ElysiumArmourVulnDischargeCallback {
    Event<ElysiumArmourVulnDischargeCallback> EVENT = EventFactory.createArrayBacked(
            ElysiumArmourVulnDischargeCallback.class,
            (callbacks) -> (armour, wearer, target) -> {
                for (ElysiumArmourVulnDischargeCallback callback : callbacks) {
                    if (callback.handleVulnDischarge(armour, wearer, target)) {
                        return true;
                    }
                }
                return false;
            }
    );

   /*Event<ElysiumArmourVulnDischargeCallback> EVENT = EventFactory.createArrayBacked(ElysiumArmourVulnDischargeCallback.class, callbacks -> (armour, wearer, target) -> {
         for (ElysiumArmourVulnDischargeCallback callback : callbacks) {
            if (callback.handleVulnDischarge(armour, wearer, target)) {
               return true;
            }
         }

         return false;
      });*/

   boolean handleVulnDischarge(ElysiumArmourComponent var1, LivingEntity var2, LivingEntity var3);
}
