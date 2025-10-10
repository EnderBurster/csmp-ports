package aureum.asta.disks.ports.charter.client.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class RivetedHornsModel extends HornsModel {
   private static final Identifier tex = new Identifier("charter", "textures/entity/horns/riveted_horns.png");
   public final ModelPart horns;

   public RivetedHornsModel(ModelPart root) {
      super(RenderLayer::getEntityTranslucent);
      this.horns = root.getChild("horns");
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData data = new ModelData();
      ModelPartData root = data.getRoot();
      ModelPartData horns = root.addChild("horns", ModelPartBuilder.create(), ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));
      ModelPartData bone = horns.addChild(
         "bone",
         ModelPartBuilder.create().uv(0, 9).cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)),
         ModelTransform.of(-3.0F, -7.0F, -4.0F, -0.0873F, 0.6109F, -0.4363F)
      );
      ModelPartData bone2 = horns.addChild(
         "bone2",
         ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -2.0F, -3.0F, 6.0F, 3.0F, 6.0F, new Dilation(0.0F)),
         ModelTransform.of(3.0F, -7.0F, -4.0F, -0.0873F, -0.6109F, 0.4363F)
      );
      return TexturedModelData.of(data, 32, 32);
   }

   @Override
   public ModelPart getHorns() {
      return this.horns;
   }

   @Override
   public Identifier getTexture() {
      return tex;
   }
}
