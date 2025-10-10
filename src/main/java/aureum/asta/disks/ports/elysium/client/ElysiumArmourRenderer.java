package aureum.asta.disks.ports.elysium.client;

import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.armour.ElysiumArmorModel;
import aureum.asta.disks.ports.elysium.armour.ElysiumArmourItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ElysiumArmourRenderer extends GeoArmorRenderer<ElysiumArmourItem> {
   public ElysiumArmourRenderer() {
      super(new ElysiumArmorModel());
   }
}
