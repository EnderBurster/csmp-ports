package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.common.init.CharterItems;
import aureum.asta.disks.ports.charter.common.item.GauntletItem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ItemRenderer.class})
abstract class ItemRendererMixin {
   @Shadow
   @Final
   private ItemModels models;

   @Shadow
   public abstract void renderItem(ItemStack var1, ModelTransformationMode var2, int var3, int var4, MatrixStack var5, VertexConsumerProvider var6, World world, int var7);

   @Inject(
      method = {"getModel"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void charter$getHeldItemModel(ItemStack stack, World world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
      if (stack.getItem() instanceof GauntletItem) {
         BakedModel bakedModel = this.models.getModelManager().getModel(ModelIdentifier.ofVanilla("trident_in_hand", "inventory"));
         ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld)world : null;
         BakedModel bakedModel2 = bakedModel.getOverrides().apply(bakedModel, stack, clientWorld, entity, seed);
         cir.setReturnValue(bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2);
      }
   }

   @Inject(
      method = {"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IILnet/minecraft/client/render/model/BakedModel;)V"},
      at = {@At("TAIL")}
   )
   private void charter$renderItem(
      ItemStack stack,
      ModelTransformationMode renderMode,
      boolean leftHanded,
      MatrixStack matrices,
      VertexConsumerProvider vertexConsumers,
      int light,
      int overlay,
      BakedModel model,
      CallbackInfo ci
   ) {
      if (stack.getItem().equals(CharterItems.HOARDER_MAW) && renderMode == ModelTransformationMode.GUI && stack.getOrCreateNbt().contains("item_nm")) {
         NbtCompound nbtCompound = stack.getOrCreateNbt();
         matrices.scale(0.7F, 0.7F, 0.7F);
         matrices.translate(0.0, 0.0, -0.5);
         ItemStack sub = new ItemStack((ItemConvertible) Registries.ITEM.get(new Identifier(nbtCompound.getString("item_nm"), nbtCompound.getString("item_path"))));
         this.renderItem(
            sub, renderMode, light / calculateSizeFromCount(nbtCompound.getInt("held_item_count"), sub.getMaxCount()), overlay, matrices, vertexConsumers, null, 0
         );
      }
   }

   @Unique
   private static int calculateSizeFromCount(int i, int b) {
      if (b == 1) {
         b = 2;
      }

      if (i > b * b * b * b) {
         return 2;
      } else if (i > b * b * b) {
         return 4;
      } else if (i > b * b) {
         return 8;
      } else {
         return i > b ? 12 : 16;
      }
   }
}
