package aureum.asta.disks.ports.elysium.client.cheirosiphon;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;

public class CheirosiphonItemRenderer implements DynamicItemRenderer, IdentifiableResourceReloadListener {
   private final Identifier id;
   private final Identifier itemId;
   private ItemRenderer itemRenderer;
   private BakedModel inventoryItemModel;
   private BakedModel worldItemModel;

   public CheirosiphonItemRenderer(Identifier itemId) {
      this.id = new Identifier(itemId.getNamespace(), itemId.getPath() + "_renderer");
      this.itemId = itemId;
   }

   public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
      matrices.pop();
      matrices.push();
      if (mode != ModelTransformationMode.FIRST_PERSON_LEFT_HAND
         && mode != ModelTransformationMode.FIRST_PERSON_RIGHT_HAND
         && mode != ModelTransformationMode.THIRD_PERSON_LEFT_HAND
         && mode != ModelTransformationMode.THIRD_PERSON_RIGHT_HAND) {
         this.itemRenderer.renderItem(stack, mode, false, matrices, vertexConsumers, light, overlay, this.inventoryItemModel);
      } else {
         boolean leftHanded = switch (mode) {
            case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> true;
            default -> false;
         };
         this.itemRenderer.renderItem(stack, mode, leftHanded, matrices, vertexConsumers, light, overlay, this.worldItemModel);
      }
   }

   @NotNull
   public Identifier getFabricId() {
      return this.id;
   }

   public CompletableFuture<Void> reload(
      Synchronizer preparationBarrier,
      ResourceManager resourceManager,
      Profiler preparationsProfiler,
      Profiler reloadProfiler,
      Executor backgroundExecutor,
      Executor gameExecutor
   ) {
      return preparationBarrier.whenPrepared(Unit.INSTANCE)
         .thenRunAsync(
            () -> {
               reloadProfiler.startTick();
               reloadProfiler.push("load_cheirosiphon_models");
               MinecraftClient client = MinecraftClient.getInstance();
               this.itemRenderer = client.getItemRenderer();
               this.inventoryItemModel = client.getBakedModelManager()
                  .getModel(new ModelIdentifier(new Identifier(this.itemId.getNamespace(), this.itemId.getPath() + "_gui"), "inventory"));
               this.worldItemModel = client.getBakedModelManager()
                  .getModel(new ModelIdentifier(new Identifier(this.itemId.getNamespace(), this.itemId.getPath() + "_held"), "inventory"));
               reloadProfiler.pop();
               reloadProfiler.endTick();
            },
            backgroundExecutor
         );
   }

   public static void setup(Item item) {
      Identifier cheirosiphonId = Registries.ITEM.getId(item);
      CheirosiphonItemRenderer cheirosiphonRenderer = new CheirosiphonItemRenderer(cheirosiphonId);
      ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(cheirosiphonRenderer);
      //ResourceReloader.get(ResourceType.CLIENT_RESOURCES).registerReloader(cheirosiphonRenderer);
      BuiltinItemRendererRegistry.INSTANCE.register(item, cheirosiphonRenderer);
      ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
         out.accept(new ModelIdentifier(new Identifier(cheirosiphonId.getNamespace(), cheirosiphonId.getPath() + "_gui"), "inventory"));
         out.accept(new ModelIdentifier(new Identifier(cheirosiphonId.getNamespace(), cheirosiphonId.getPath() + "_held"), "inventory"));
      });
   }

   public static void animateHold(ModelPart rightArm, ModelPart leftArm, ModelPart head, boolean rightHanded) {
      ModelPart mainHand = rightHanded ? rightArm : leftArm;
      ModelPart offHand = rightHanded ? leftArm : rightArm;
      mainHand.yaw = (rightHanded ? -0.1F : 0.1F) + head.yaw;
      offHand.yaw = (rightHanded ? 0.7F : -0.7F) + head.yaw;
      mainHand.pitch = (float) (-Math.PI / 2) + head.pitch + 0.1F;
      offHand.pitch = -1.5F + head.pitch;
   }

   public static void animateOffhandHold(ModelPart rightArm, ModelPart leftArm, ModelPart head, boolean rightHanded) {
      ModelPart offHand = rightHanded ? leftArm : rightArm;
      offHand.yaw = (rightHanded ? -0.1F : 0.1F) + head.yaw;
      offHand.pitch = (float) (-Math.PI / 2) + head.pitch + 0.1F;
   }
}
