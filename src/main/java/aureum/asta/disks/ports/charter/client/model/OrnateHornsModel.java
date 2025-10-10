package aureum.asta.disks.ports.charter.client.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class OrnateHornsModel extends HornsModel {
   private static final Identifier tex = new Identifier("charter", "textures/entity/horns/pipe_horns.png");
   public final ModelPart horns;

   public OrnateHornsModel(ModelPart root) {
      super(RenderLayer::getEntityTranslucent);
      this.horns = root.getChild("horns");
   }

   @Override
   public Identifier getTexture() {
      return tex;
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData data = new ModelData();
      ModelPartData root = data.getRoot();
      ModelPartData horns = root.addChild("horns", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
      ModelPartData cube_r1 = horns.addChild(
         "cube_r1",
         ModelPartBuilder.create().uv(0, 0).cuboid(-1.5F, -9.0F, 0.0F, 3.0F, 8.0F, 8.0F, new Dilation(0.0F)),
         ModelTransform.of(2.0F, -6.0F, -2.0F, -0.7854F, 0.7854F, 0.0F)
      );
      ModelPartData cube_r2 = horns.addChild(
         "cube_r2",
         ModelPartBuilder.create().uv(14, 8).cuboid(-1.5F, -9.0F, 0.0F, 3.0F, 8.0F, 8.0F, new Dilation(0.0F)),
         ModelTransform.of(-2.0F, -6.0F, -2.0F, -0.7854F, -0.7854F, 0.0F)
      );
      return TexturedModelData.of(data, 64, 64);
   }

   @Override
   public ModelPart getHorns() {
      return this.horns;
   }
}
