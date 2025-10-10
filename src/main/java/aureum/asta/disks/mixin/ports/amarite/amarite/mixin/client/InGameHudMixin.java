package aureum.asta.disks.mixin.ports.amarite.amarite.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.gui.hud.InGameHud.HeartType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEntities;

@Mixin({InGameHud.class})
public abstract class InGameHudMixin extends DrawableHelper {
   @Unique
   private static final Identifier AMARITE_ICONS_TEXTURE = Amarite.id("textures/gui/icons.png");
   @Unique
   private final Set<Long> containers = new HashSet<>();

   @Inject(
      method = {"renderHealthBar"},
      at = {@At("HEAD")}
   )
   private void amarite$resetSet(
      MatrixStack matrices,
      PlayerEntity player,
      int x,
      int y,
      int lines,
      int regeneratingHeartIndex,
      float maxHealth,
      int lastHealth,
      int health,
      int absorption,
      boolean blinking,
      CallbackInfo ci
   ) {
      this.containers.clear();
   }

   @WrapOperation(
      method = {"drawHeart"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
      )}
   )
   private void amarite$buddingHearts(
      MatrixStack matrices,
      int x,
      int y,
      int u,
      int v,
      int width,
      int height,
      Operation<Void> original,
      MatrixStack hmatrices,
      HeartType type,
      int hx,
      int hy,
      int hv,
      boolean blinking,
      boolean halfHeart
   ) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && player.hasStatusEffect(AmariteEntities.BUDDING)) {
         if (type == HeartType.CONTAINER) {
            this.containers.add((long)x << 32 | (long)y);
            return;
         }

         this.containers.remove((long)x << 32 | (long)y);
         RenderSystem.setShaderTexture(0, AMARITE_ICONS_TEXTURE);
         int renderU = halfHeart ? height : 0;
         int renderV = blinking ? 9 : 0;
         drawTexture(matrices, x, y, (float)renderU, (float)renderV, width, height, 32, 32);
         RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
      } else {
         original.call(matrices, x, y, u, v, width, height);
      }
   }

   @Inject(
      method = {"renderHealthBar"},
      at = {@At("TAIL")}
   )
   private void amarite$containers(
      MatrixStack matrices,
      PlayerEntity player,
      int x,
      int y,
      int lines,
      int regeneratingHeartIndex,
      float maxHealth,
      int lastHealth,
      int health,
      int absorption,
      boolean blinking,
      CallbackInfo ci
   ) {
      RenderSystem.setShaderTexture(0, AMARITE_ICONS_TEXTURE);

      for (Long container : this.containers) {
         int cX = (int)(container >> 32);
         int cY = (int)(container & 4294967295L);
         drawTexture(matrices, cX, cY, 0.0F, 18.0F, 9, 9, 32, 32);
      }

      RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
   }
}
