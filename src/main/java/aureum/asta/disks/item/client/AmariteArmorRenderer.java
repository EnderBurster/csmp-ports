package aureum.asta.disks.item.client;

import aureum.asta.disks.item.custom.AmariteArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class AmariteArmorRenderer extends GeoArmorRenderer<AmariteArmorItem> {
    public AmariteArmorRenderer() {
        super(new AmariteArmorModel());
    }
}
