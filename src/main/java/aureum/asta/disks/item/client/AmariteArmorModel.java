package aureum.asta.disks.item.client;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.item.custom.AmariteArmorItem;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.GeoModel;

public class AmariteArmorModel extends GeoModel<AmariteArmorItem> {
    @Override
    public Identifier getModelResource(AmariteArmorItem amariteArmorItem) {
        return new Identifier(AureumAstaDisks.MOD_ID, "geo/amarite_armour.geo.json");
    }

    @Override
    public Identifier getTextureResource(AmariteArmorItem amariteArmorItem) {
        return new Identifier(AureumAstaDisks.MOD_ID, "textures/armor/amarite_armour.png");
    }

    @Override
    public Identifier getAnimationResource(AmariteArmorItem amariteArmorItem) {
        return new Identifier(AureumAstaDisks.MOD_ID, "animations/amarite_armour.animation.json");
    }
}
