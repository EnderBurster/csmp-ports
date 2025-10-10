package aureum.asta.disks.ports.elysium.armour;

import aureum.asta.disks.ports.elysium.Elysium;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class ElysiumArmorModel extends GeoModel<ElysiumArmourItem> {
    @Override
    public Identifier getModelResource(ElysiumArmourItem elysiumArmourItem) {
        return Elysium.id("geo/elysium_armour.geo.json");
    }

    @Override
    public Identifier getTextureResource(ElysiumArmourItem elysiumArmourItem) {
        return Elysium.id("textures/model/elysium_armour.png");
    }

    @Override
    public Identifier getAnimationResource(ElysiumArmourItem elysiumArmourItem) {
        return Elysium.id("animations/elysium_armour.animation.json");
    }
}
