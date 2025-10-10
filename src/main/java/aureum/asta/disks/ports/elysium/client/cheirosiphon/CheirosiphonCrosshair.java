package aureum.asta.disks.ports.elysium.client.cheirosiphon;

import com.mojang.blaze3d.systems.RenderSystem;
import aureum.asta.disks.ports.elysium.Elysium;
import aureum.asta.disks.ports.elysium.cheirosiphon.HeatingItemsComponent;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public final class CheirosiphonCrosshair {
   private static final Identifier CHEIROSIPHON_CROSSHAIR_LOCATION = Elysium.id("textures/gui/cheirosiphon_crosshair.png");

   public static void render(PlayerEntity player, MatrixStack poseStack, int x, int y) {
      int heat = ((HeatingItemsComponent)player.getComponent(HeatingItemsComponent.KEY)).getHeat(Elysium.CHEIROSIPHON);
      float progress = (float)heat / (float)Elysium.CHEIROSIPHON.getMaxHeat();
      int height1 = (int)(13.0F * (1.0F - progress));
      int height2 = (int)(13.0F * progress);
      RenderSystem.setShaderTexture(0, CHEIROSIPHON_CROSSHAIR_LOCATION);
      DrawableHelper.drawTexture(poseStack, x, y, 0, 0.0F, 0.0F, 37, height1, 64, 64);
      RenderSystem.defaultBlendFunc();
      DrawableHelper.drawTexture(poseStack, x, y + height1, 0, 0.0F, (float)(13 + height1), 37, height2, 64, 64);
   }
}
