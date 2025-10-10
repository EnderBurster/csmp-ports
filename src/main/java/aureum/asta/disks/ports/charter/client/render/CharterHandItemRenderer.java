package aureum.asta.disks.ports.charter.client.render;

import aureum.asta.disks.mixin.ports.charter.PlayerSkinProviderAccessor;
import aureum.asta.disks.ports.amarite.mialib.util.Vec3f;
import aureum.asta.disks.ports.charter.CharterClient;
import aureum.asta.disks.ports.charter.client.model.HandModel;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.DynamicItemRenderer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader.Synchronizer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.Uuids;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.client.render.model.json.ModelTransformationMode.FIRST_PERSON_LEFT_HAND;
import static net.minecraft.client.render.model.json.ModelTransformationMode.THIRD_PERSON_LEFT_HAND;

public class CharterHandItemRenderer implements DynamicItemRenderer, IdentifiableResourceReloadListener {
   private final Identifier id;
   protected final Identifier handId;
   protected ItemRenderer itemRenderer;
   protected HandModel hand;

   public CharterHandItemRenderer(Identifier handId) {
      this.id = new Identifier(handId.getNamespace(), handId.getPath() + "_renderer");
      this.handId = handId;
   }

   @NotNull
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
         this.hand = new HandModel(client.getEntityModelLoader().getModelPart(CharterClient.HAND_MODEL_LAYER), false);
         applyProfiler.pop();
         applyProfiler.endTick();
      }, applyExecutor);
   }

   public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
      MinecraftClient cli = MinecraftClient.getInstance();
      Identifier texture = null;
      GameProfile profile = null;
      boolean[] slim = new boolean[]{false};
      if (stack.getOrCreateNbt().contains("owner") && NbtHelper.toGameProfile(stack.getOrCreateNbt().getCompound("owner")) != null) {
         profile = NbtHelper.toGameProfile(stack.getOrCreateNbt().getCompound("owner"));
         if (cli.world.getPlayerByUuid(profile.getId()) instanceof AbstractClientPlayerEntity cliPlayer) {
            texture = cliPlayer.getSkinTexture();
            slim[0] = cliPlayer.getModel().contains("slim");
         } else {
            Map<Type, MinecraftProfileTexture> map = cli.getSkinProvider().getTextures(profile);
            texture = map.containsKey(Type.SKIN)
               ? ((PlayerSkinProviderAccessor)cli.getSkinProvider()).invokeLoadSkin(map.get(Type.SKIN), Type.SKIN, (type, id, tex) -> {
                  if (type == Type.SKIN) {
                     String str = tex.getMetadata("model");
                     if (str == null) {
                        str = "default";
                     }

                     slim[0] = str.contains("slim");
                  }
               })
               : DefaultSkinHelper.getTexture(Uuids.getUuidFromProfile(profile));
         }
      } else {
         texture = DefaultSkinHelper.getTexture();
      }

      boolean bl = slim[0];
      if (this.hand.slim != bl) {
         if (bl) {
            this.hand = new HandModel(cli.getEntityModelLoader().getModelPart(CharterClient.HAND_MODEL_LAYER_SLIM), true);
         } else {
            this.hand = new HandModel(cli.getEntityModelLoader().getModelPart(CharterClient.HAND_MODEL_LAYER), false);
         }
      }
      boolean var17 = switch (mode) {
         case FIRST_PERSON_LEFT_HAND, THIRD_PERSON_LEFT_HAND -> true;
         default -> false;
      };
      matrices.push();
      matrices.scale(1.0F, -1.0F, -1.0F);
      if (var17) {
         matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternionf(180.0F));
      }

      matrices.translate(var17 ? -0.4F : 0.4F, -0.8F, var17 ? 0.5 : -0.5);
      VertexConsumer vertexConsumer = ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, this.hand.getLayer(texture), false, stack.hasGlint());
      this.hand.render(matrices, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
      matrices.pop();
   }
}
