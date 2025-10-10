package aureum.asta.disks.api.lodestone.systems.rendering;

import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderPhase.Transparency;

public class Phases extends RenderPhase {
   public static final Transparency ADDITIVE_TRANSPARENCY = new Transparency("additive_transparency", () -> {
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.depthMask(true);
   });
   public static final Transparency NORMAL_TRANSPARENCY = new Transparency("normal_transparency", () -> {
      RenderSystem.depthMask(false);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
   }, () -> {
      RenderSystem.disableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.depthMask(true);
   });

   public Phases(String string, Runnable runnable, Runnable runnable2) {
      super(string, runnable, runnable2);
   }
}
