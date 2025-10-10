package aureum.asta.disks.ports.charter.client.render;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader.Synchronizer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

public class GauntletItemRenderer implements DynamicItemRenderer, IdentifiableResourceReloadListener {
   private final Identifier id;
   private final Identifier gauntletId;
   private ItemRenderer itemRenderer;
   private BakedModel inventoryModel;
   private BakedModel worldModel;
   private BakedModel leftWorldModel;

   public GauntletItemRenderer(Identifier gauntletId) {
      this.id = new Identifier(gauntletId.getNamespace(), gauntletId.getPath() + "_renderer");
      this.gauntletId = gauntletId;
   }

   public Identifier getFabricId() {
      return this.id;
   }

   public CompletableFuture<Void> reload(
      Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor
   ) {
      return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> {
         applyProfiler.startTick();
         applyProfiler.push("listener");
         MinecraftClient client = MinecraftClient.getInstance();
         this.itemRenderer = client.getItemRenderer();
         this.inventoryModel = client.getBakedModelManager().getModel(new ModelIdentifier(this.gauntletId.getNamespace(),this.gauntletId.getPath() + "_gui", "inventory"));
         this.worldModel = client.getBakedModelManager().getModel(new ModelIdentifier(this.gauntletId.getNamespace(), this.gauntletId.getPath() + "_handheld", "inventory"));
         this.leftWorldModel = client.getBakedModelManager().getModel(new ModelIdentifier(this.gauntletId.getNamespace(), this.gauntletId.getPath() + "_handheld_left", "inventory"));
         applyProfiler.pop();
         applyProfiler.endTick();
      }, applyExecutor);
   }

   public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
      matrices.pop();
      matrices.push();
      if (mode != ModelTransformationMode.FIRST_PERSON_LEFT_HAND
         && mode != ModelTransformationMode.FIRST_PERSON_RIGHT_HAND
         && mode != ModelTransformationMode.THIRD_PERSON_LEFT_HAND
         && mode != ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) {
         this.itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryModel);
      } else {
         boolean leftHanded = switch (mode) {
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> true;
            default -> false;
         };
         matrices.pop();
         matrices.push();
         this.itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, leftHanded ? this.leftWorldModel : this.worldModel);
      }
   }
}
