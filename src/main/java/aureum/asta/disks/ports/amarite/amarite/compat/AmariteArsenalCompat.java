package aureum.asta.disks.ports.amarite.amarite.compat;

import aureum.asta.disks.util.WeaponSlotCallback;
import net.minecraft.util.ActionResult;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;

public class AmariteArsenalCompat {
   public static void init() {
      WeaponSlotCallback.EVENT.register((WeaponSlotCallback)(player, stack) -> stack.getItem() == AmariteItems.AMARITE_LONGSWORD ? ActionResult.FAIL : ActionResult.PASS);
   }
}
