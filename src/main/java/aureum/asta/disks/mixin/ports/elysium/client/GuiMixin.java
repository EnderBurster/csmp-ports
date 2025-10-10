package aureum.asta.disks.mixin.ports.elysium.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.client.cheirosiphon.CheirosiphonCrosshair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin({InGameHud.class})
public class GuiMixin extends DrawableHelper {
   @Shadow
   private int scaledWidth;
   @Shadow
   private int scaledHeight;

   @WrapOperation(
      method = {"renderCrosshair"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V"
      )}
   )
   private void elysium$renderCheirosiphonCrosshair(MatrixStack poseStack, int x, int y, int u, int v, int w, int h, Operation<Void> original) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      int xDifference = -11;
      int yDifference = 1;
      if (player != null && player.isHolding(Elysium.CHEIROSIPHON)) {
         CheirosiphonCrosshair.render(player, poseStack, x + xDifference, y + yDifference);
         RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);
         RenderSystem.blendFuncSeparate(SrcFactor.ONE_MINUS_DST_COLOR, DstFactor.ONE_MINUS_SRC_COLOR, SrcFactor.ONE, DstFactor.ZERO);
      } else {
         original.call(poseStack, x, y, u, v, w, h);
      }
   }
}
