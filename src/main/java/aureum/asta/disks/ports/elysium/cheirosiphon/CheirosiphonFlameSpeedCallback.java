package aureum.asta.disks.ports.elysium.cheirosiphon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface CheirosiphonFlameSpeedCallback {
   Event<CheirosiphonFlameSpeedCallback> EVENT = EventFactory.createArrayBacked(CheirosiphonFlameSpeedCallback.class, callbacks -> (user, stack, speed) -> {
         for (CheirosiphonFlameSpeedCallback callback : callbacks) {
            speed = callback.modifySpeed(user, stack, speed);
         }

         return speed;
      });

   float modifySpeed(LivingEntity var1, ItemStack var2, float var3);
}
