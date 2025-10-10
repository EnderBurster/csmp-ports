package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.client;

import net.minecraft.entity.ItemEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.mialib.MiaLibClient;

@Mixin({ItemEntityRenderer.class})
public class ItemEntityRendererMixin {
   @Inject(
      method = {"render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"},
      at = {@At("HEAD")}
   )
   private void mialib$rendering(ItemEntity itemEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
      MiaLibClient.currentMode = ModelTransformationMode.GROUND;
   }
}
