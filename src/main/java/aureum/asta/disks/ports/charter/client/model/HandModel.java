package aureum.asta.disks.ports.charter.client.model;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class HandModel extends Model {
   private final ModelPart root;
   protected final ModelPart leftArm;
   protected final ModelPart leftSleeve;
   public final boolean slim;

   public HandModel(ModelPart modelPart, boolean slim) {
      super(RenderLayer::getEntityTranslucent);
      this.root = modelPart;
      this.leftArm = modelPart.getChild("left_arm");
      this.leftSleeve = modelPart.getChild("left_sleeve");
      this.slim = slim;
   }

   public static ModelData getModelData() {
      return new ModelData();
   }

   public static ModelData getModelData(Dilation dilation, boolean slim) {
      ModelData modelData = getModelData();
      ModelPartData modelPartData = modelData.getRoot();
      if (slim) {
         modelPartData.addChild(
            "left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(0.0F, 2.5F, 0.0F)
         );
         modelPartData.addChild(
            "left_sleeve",
            ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, dilation.add(0.25F)),
            ModelTransform.pivot(0.0F, 2.5F, 0.0F)
         );
      } else {
         modelPartData.addChild(
            "left_arm", ModelPartBuilder.create().uv(32, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation), ModelTransform.pivot(0.0F, 2.0F, 0.0F)
         );
         modelPartData.addChild(
            "left_sleeve",
            ModelPartBuilder.create().uv(48, 48).cuboid(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, dilation.add(0.25F)),
            ModelTransform.pivot(0.0F, 2.0F, 0.0F)
         );
      }

      return modelData;
   }

   public static TexturedModelData getTexturedModelData(boolean slim) {
      ModelData modelData = getModelData(Dilation.NONE, slim);
      return TexturedModelData.of(modelData, 64, 64);
   }

   public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
   }
}
