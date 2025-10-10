package aureum.asta.disks.ports.elysium.cheirosiphon;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface CheirosiphonAirblastCallback {
   Event<CheirosiphonAirblastCallback> EVENT = EventFactory.createArrayBacked(CheirosiphonAirblastCallback.class, callbacks -> (user, cheirosiphon) -> {
         for (CheirosiphonAirblastCallback callback : callbacks) {
            if (callback.handleAirblast(user, cheirosiphon)) {
               return true;
            }
         }

         return false;
      });

   boolean handleAirblast(LivingEntity var1, ItemStack var2);
}
