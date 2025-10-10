package aureum.asta.disks.ports.elysium.cheirosiphon;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface CheirosiphonFlameSpawningCallback {
   Event<CheirosiphonFlameSpawningCallback> EVENT = EventFactory.createArrayBacked(CheirosiphonFlameSpawningCallback.class, callbacks -> (user, stack, flame) -> {
         for (CheirosiphonFlameSpawningCallback callback : callbacks) {
            callback.acceptFlame(user, stack, flame);
         }
      });

   void acceptFlame(LivingEntity var1, ItemStack var2, CheirosiphonFlame var3);
}
