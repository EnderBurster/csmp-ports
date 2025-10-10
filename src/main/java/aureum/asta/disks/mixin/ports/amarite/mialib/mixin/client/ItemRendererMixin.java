package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.client;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.mialib.MiaLibClient;

@Mixin({ItemRenderer.class})
public class ItemRendererMixin {
   @Inject(
      method = {"renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/world/World;III)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/ItemRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;"
      )}
   )
   private void mialib$storeEntity(
      LivingEntity entity,
      ItemStack item,
      ModelTransformationMode renderMode,
      boolean leftHanded,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      World world,
      int light,
      int overlay,
      int seed,
      CallbackInfo ci
   ) {
      MiaLibClient.renderingEntityWithItem = entity;
      MiaLibClient.currentMode = renderMode;
   }

   @Inject(
      method = {"innerRenderInGui(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/ItemRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;"
      )}
   )
   private void mialib$storeEntity(MatrixStack matrices, LivingEntity entity, World world, ItemStack stack, int x, int y, int seed, int depth, CallbackInfo ci) {
      MiaLibClient.renderingEntityWithItem = entity;
      MiaLibClient.currentMode = ModelTransformationMode.GUI;
   }

   @Inject(
      method = {"renderGuiItemIcon"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/ItemRenderer;getModel(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;I)Lnet/minecraft/client/render/model/BakedModel;"
      )}
   )
   private void mialib$storeEntity(MatrixStack matrices, ItemStack stack, int x, int y, CallbackInfo ci) {
      MiaLibClient.renderingEntityWithItem = null;
      MiaLibClient.currentMode = ModelTransformationMode.GUI;
   }

   @Inject(
      method = {"renderGuiItemOverlay(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z",
         shift = Shift.BEFORE
      )}
   )
   public void mialib$customBar(MatrixStack matrices, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, CallbackInfo ci) {
      ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
      if (clientPlayerEntity != null) {
         stack.getItem().mialib$renderCustomBar((ItemRenderer)(Object)this, renderer, stack, x, y, countLabel, matrices);
      }
   }
}
