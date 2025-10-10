package aureum.asta.disks.ports.charter.client.model;

import aureum.asta.disks.ports.charter.common.entity.living.BloodflyEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class BloodflyEntityModel extends EntityModel<BloodflyEntity> {
   public final ModelPart root;
   private final ModelPart head;
   private final ModelPart leftArm;
   private final ModelPart rightArm;
   private final ModelPart lWing1;
   private final ModelPart lWing2;
   private final ModelPart rWing1;
   private final ModelPart rWing2;

   public BloodflyEntityModel(ModelPart root) {
      this.root = root.getChild("root");
      this.head = this.root.getChild("head");
      this.leftArm = this.root.getChild("lArm");
      this.rightArm = this.root.getChild("rArm");
      this.lWing1 = this.root.getChild("wings").getChild("lWing1");
      this.lWing2 = this.root.getChild("wings").getChild("lWing2");
      this.rWing1 = this.root.getChild("wings").getChild("rWing1");
      this.rWing2 = this.root.getChild("wings").getChild("rWing2");
   }

   public static TexturedModelData getTexturedModelData() {
      ModelData data = new ModelData();
      ModelPartData ModelPartData = data.getRoot();
      ModelPartData root = ModelPartData.addChild(
         "root", ModelPartBuilder.create().uv(38, 50).cuboid(-2.5F, 0.0F, 0.0F, 5.0F, 3.0F, 2.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 16.0F, 0.0F)
      );
      ModelPartData head = root.addChild(
         "head", ModelPartBuilder.create().uv(40, 39).cuboid(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 3.0F, new Dilation(0.0F)), ModelTransform.pivot(0.0F, 2.0F, 0.0F)
      );
      ModelPartData rMandible = head.addChild(
         "rMandible",
         ModelPartBuilder.create().uv(0, 52).cuboid(0.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
         ModelTransform.of(-3.0F, 4.0F, -2.0F, 0.0F, 0.0F, -0.0873F)
      );
      ModelPartData lMandible = head.addChild(
         "lMandible",
         ModelPartBuilder.create().uv(51, 22).cuboid(-2.0F, 0.0F, -1.0F, 2.0F, 3.0F, 2.0F, new Dilation(0.0F)),
         ModelTransform.of(3.0F, 4.0F, -2.0F, 0.0F, 0.0F, 0.0873F)
      );
      ModelPartData rArm = root.addChild(
         "rArm",
         ModelPartBuilder.create().uv(20, 48).cuboid(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new Dilation(0.0F)),
         ModelTransform.of(-2.5F, 2.0F, 0.0F, 0.0F, 0.0F, -0.9599F)
      );
      ModelPartData rHand = rArm.addChild(
         "rHand",
         ModelPartBuilder.create().uv(40, 22).cuboid(-0.75F, -6.0F, -2.5F, 3.0F, 12.0F, 5.0F, new Dilation(0.0F)),
         ModelTransform.of(-8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F)
      );
      ModelPartData lArm = root.addChild(
         "lArm",
         ModelPartBuilder.create().uv(0, 48).cuboid(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F, new Dilation(0.0F)),
         ModelTransform.of(2.5F, 2.0F, 0.0F, 0.0F, 0.0F, 0.9599F)
      );
      ModelPartData rHand2 = lArm.addChild(
         "rHand2",
         ModelPartBuilder.create().uv(40, 0).cuboid(-2.3F, -10.0F, -3.0F, 3.0F, 16.0F, 6.0F, new Dilation(0.0F)),
         ModelTransform.of(8.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F)
      );
      ModelPartData wings = root.addChild("wings", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 8.0F, 0.0F));
      ModelPartData lWing1 = wings.addChild(
         "lWing1",
         ModelPartBuilder.create().uv(20, 24).cuboid(-1.0F, -24.0F, 0.0F, 10.0F, 24.0F, 0.0F, new Dilation(0.0F)),
         ModelTransform.of(1.0F, -8.0F, 1.0F, -0.1309F, -0.4363F, 0.6981F)
      );
      ModelPartData lWing2 = wings.addChild(
         "lWing2",
         ModelPartBuilder.create().uv(0, 24).cuboid(-1.0F, -24.0F, 0.0F, 10.0F, 24.0F, 0.0F, new Dilation(0.0F)),
         ModelTransform.of(2.0F, -8.0F, 1.0F, -0.1745F, -0.3054F, 1.309F)
      );
      ModelPartData rWing1 = wings.addChild(
         "rWing1",
         ModelPartBuilder.create().uv(20, 0).cuboid(-9.0F, -24.0F, 0.0F, 10.0F, 24.0F, 0.0F, new Dilation(0.0F)),
         ModelTransform.of(-1.0F, -8.0F, 1.0F, -0.1309F, 0.4363F, -0.6981F)
      );
      ModelPartData rWing2 = wings.addChild(
         "rWing2",
         ModelPartBuilder.create().uv(0, 0).cuboid(-9.0F, -24.0F, 0.0F, 10.0F, 24.0F, 0.0F, new Dilation(0.0F)),
         ModelTransform.of(-2.0F, -8.0F, 1.0F, -0.1745F, 0.3054F, -1.309F)
      );
      return TexturedModelData.of(data, 64, 64);
   }

   public void animateModel(BloodflyEntity entity, float limbAngle, float limbDistance, float tickDelta) {
      this.root.pitch = (float)(entity.getVelocity().length() * 0.6F);
      this.rightArm.pitch = (float)(entity.getVelocity().length() * 0.6F);
      this.leftArm.pitch = (float)(entity.getVelocity().length() * 0.6F);
   }

   public void setAngles(BloodflyEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
      float k = animationProgress * 240.64226F * (float) (Math.PI / 180.0);
      this.lWing1.roll = 0.6981F + MathHelper.cos(k) * (float) Math.PI * 0.1F;
      this.lWing2.roll = 1.309F + MathHelper.cos(k) * (float) Math.PI * 0.1F;
      this.rWing1.roll = -0.6981F - MathHelper.cos(k) * (float) Math.PI * 0.1F;
      this.rWing2.roll = -1.309F - MathHelper.cos(k) * (float) Math.PI * 0.1F;
   }

   public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.root.render(matrices, vertices, light, overlay, red, green, blue, alpha);
   }
}
