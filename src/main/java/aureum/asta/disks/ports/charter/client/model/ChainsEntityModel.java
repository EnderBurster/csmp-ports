package aureum.asta.disks.ports.charter.client.model;

import aureum.asta.disks.ports.charter.common.entity.ChainsEntity;
import java.util.Arrays;
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
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class ChainsEntityModel extends EntityModel<ChainsEntity> {
   private final ModelPart root;
   private final ModelPart[] chains = new ModelPart[5];

   public ChainsEntityModel(ModelPart root) {
      super(RenderLayer::getEntityTranslucent);
      this.root = root.getChild("root");
      Arrays.setAll(this.chains, index -> root.getChild("root").getChild(getChainName(index)));
   }

   private static String getChainName(int index) {
      return "chain" + (index + 1);
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData data = new ModelData();
      ModelPartData rootPart = data.getRoot();
      ModelPartData root = rootPart.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 12.0F, 0.0F));
      ModelPartData chain1 = root.addChild(
         "chain1",
         ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F)),
         ModelTransform.pivot(0.0F, 8.5F, 0.0F)
      );
      ModelPartData chain2 = root.addChild(
         "chain2",
         ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F)),
         ModelTransform.pivot(0.0F, 3.5F, 0.0F)
      );
      ModelPartData chain3 = root.addChild(
         "chain3",
         ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F)),
         ModelTransform.pivot(0.0F, -1.5F, 0.0F)
      );
      ModelPartData chain4 = root.addChild(
         "chain4",
         ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F)),
         ModelTransform.pivot(0.0F, -6.5F, 0.0F)
      );
      ModelPartData chain5 = root.addChild(
         "chain5",
         ModelPartBuilder.create().uv(0, 0).cuboid(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new Dilation(0.0F)),
         ModelTransform.pivot(0.0F, -11.5F, 0.0F)
      );
      return TexturedModelData.of(data, 64, 64);
   }

   public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
   }

   public void setAngles(ChainsEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
   }

   public void animateModel(ChainsEntity entity, float limbAngle, float limbDistance, float tickDelta) {
      for (int j = 0; j < this.chains.length; j++) {
         this.chains[j].yaw = j % 2 == 0 ? -(((float)entity.age + tickDelta) * 0.3F) : ((float)entity.age + tickDelta) * 0.3F;
      }
   }
}
