package aureum.asta.disks.ports.amarite.amarite.client;


import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.entities.PylonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.util.Identifier;

public class PylonEntityRenderer extends ProjectileEntityRenderer<PylonEntity> {
   public static final Identifier TEXTURE = Amarite.id("textures/entity/pylon.png");

   public PylonEntityRenderer(EntityRendererFactory.Context context) {
      super(context);
   }

   public Identifier getTexture(PylonEntity arrowEntity) {
      return TEXTURE;
   }
}
