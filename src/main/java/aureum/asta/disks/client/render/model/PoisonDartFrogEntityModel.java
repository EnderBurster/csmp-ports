package aureum.asta.disks.client.render.model;

import aureum.asta.disks.ports.pickyourpoison.PickYourPoison;
import aureum.asta.disks.entity.PoisonDartFrogEntity;
import net.minecraft.util.Identifier;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

import java.util.Locale;

public class PoisonDartFrogEntityModel extends DefaultedEntityGeoModel<PoisonDartFrogEntity> {
    public PoisonDartFrogEntityModel() {
        super(PickYourPoison.id("poison_dart_frog"));
    }

    @Override
    public Identifier getTextureResource(PoisonDartFrogEntity poisonDartFrog) {
        return PickYourPoison.id("textures/entity/" + poisonDartFrog.getPoisonDartFrogType().toString().toLowerCase(Locale.ROOT) + ".png");
    }
}
