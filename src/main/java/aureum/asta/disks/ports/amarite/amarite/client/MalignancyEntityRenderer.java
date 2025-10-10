package aureum.asta.disks.ports.amarite.amarite.client;


import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.entities.MalignancyEntity;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteItems;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class MalignancyEntityRenderer extends EntityRenderer<MalignancyEntity> {
   private static final ItemStack stack = AmariteItems.AMARITE_LONGSWORD.getDefaultStack();
   private final ItemRenderer itemRenderer;
   public static final Identifier TEXTURE = Amarite.id("textures/item/amarite_longsword_malignant.png");

   public MalignancyEntityRenderer(EntityRendererFactory.Context context) {
      super(context);
      this.itemRenderer = context.getItemRenderer();
   }

   public void render(@NotNull MalignancyEntity malignancy, float f, float tickDelta, @NotNull MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
      matrixStack.push();
      matrixStack.translate(0.0, 0.8, 0.0);
      matrixStack.scale(1.7F, 1.7F, 1.7F);
      OutlineVertexConsumerProvider consumers = MinecraftClient.getInstance().getBufferBuilders().getOutlineVertexConsumers();
      if (malignancy.age > 80) {
         malignancy.rotation += 8.0F * tickDelta;
         stack.getOrCreateNbt().putInt("malignant", -10639338);
         int r = 93;
         int g = 168;
         int b = 22;
         consumers.setColor(r, g, b, 255);
         if (malignancy.firing) {
            malignancy.rotation += 8.0F * tickDelta;
         }
      } else {
         malignancy.rotation += 4.0F * tickDelta;
         int shade = (int)(160.0 + 95.0 * Math.pow(malignancy.age / 80.0F, 8.0));
         int r = shade >> 16 & 0xFF;
         int g = shade >> 8 & 0xFF;
         int b = shade & 0xFF;
         int rgb = r << 16 | g << 8 | b;
         stack.getOrCreateNbt().putInt("malignant", rgb);
         consumers.setColor(shade, shade, shade / 2, 255);
      }

      matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternionf(malignancy.rotation));
      matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternionf(-45.0F));
      this.itemRenderer.renderItem(stack, ModelTransformationMode.FIXED, 15728640, OverlayTexture.DEFAULT_UV, matrixStack, consumers, null, 0);
      matrixStack.pop();
   }

   public Identifier getTexture(MalignancyEntity malignancy) {
      return TEXTURE;
   }
}
