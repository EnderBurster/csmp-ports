package aureum.asta.disks.ports.elysium.client;

import aureum.asta.disks.api.lodestone.handlers.RenderHandler;
import com.mojang.blaze3d.platform.GlStateManager.DstFactor;
import com.mojang.blaze3d.platform.GlStateManager.SrcFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import aureum.asta.disks.ports.elysium.Elysium;
import java.util.function.Function;

import ladysnake.satin.api.event.EntitiesPreRenderCallback;
import ladysnake.satin.api.event.PostWorldRenderCallbackV2;
import ladysnake.satin.api.managed.ManagedFramebuffer;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.util.RenderLayerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

public final class GlowEffectManager implements EntitiesPreRenderCallback, PostWorldRenderCallbackV2 {
   public static final GlowEffectManager INSTANCE = new GlowEffectManager();
   private final MinecraftClient client = MinecraftClient.getInstance();
   private final ManagedShaderEffect auraPostShader = ShaderEffectManager.getInstance().manage(Elysium.id("shaders/post/glow.json"));
   public final ManagedFramebuffer auraFramebuffer = this.auraPostShader.getTarget("glows");
   private boolean auraBufferCleared;

   public void init() {
      EntitiesPreRenderCallback.EVENT.register(this);
      PostWorldRenderCallbackV2.EVENT.register(this);
   }

   public void beforeEntitiesRender(@NotNull Camera camera, @NotNull Frustum frustum, float tickDelta) {
      this.auraBufferCleared = false;
   }

   public void onWorldRendered(MatrixStack matrices, Camera camera, float tickDelta, long nanoTime) {
      if (this.auraBufferCleared) {
         this.auraPostShader.render(tickDelta);
         this.client.getFramebuffer().beginWrite(true);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(SrcFactor.SRC_ALPHA, DstFactor.ONE_MINUS_SRC_ALPHA);
         this.auraFramebuffer.draw();
         RenderSystem.disableBlend();
      }
   }

   public void beginGlowFramebufferUse() {
      Framebuffer auraFramebuffer = this.auraFramebuffer.getFramebuffer();
      if (auraFramebuffer != null) {
         auraFramebuffer.beginWrite(false);
         if (!this.auraBufferCleared) {
            float[] clearColor = auraFramebuffer.clearColor;
            RenderSystem.clearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
            RenderSystem.clear(16384, MinecraftClient.IS_SYSTEM_MAC);
            this.auraFramebuffer.copyDepthFrom(this.client.getFramebuffer());
            auraFramebuffer.beginWrite(false);
            this.auraBufferCleared = true;
         }
      }
   }

   private void endGlowFramebufferUse() {
      this.client.getFramebuffer().beginWrite(false);
   }

   public static RenderLayer getRenderType(RenderLayer base) {
      return GlowRenderTypes.getRenderType(base);
   }

   public static RenderLayer unwrap(RenderLayer base) {
      return GlowRenderTypes.unwrap(base);
   }

   public static RenderLayer getRenderType() {
      return GlowRenderTypes.DEFAULT_GLOW_LAYER;
   }

   private static final class GlowRenderTypes extends RenderLayer {
      private static final Target GLOW_TARGET = new Target(
         "elysium:glow_target", GlowEffectManager.INSTANCE::beginGlowFramebufferUse, GlowEffectManager.INSTANCE::endGlowFramebufferUse
      );
      private static final Function<Identifier, RenderLayer> GLOW_LAYER_FUNC = Util.memoize(
         id -> RenderLayer.of(
               "glow",
               VertexFormats.POSITION_COLOR_TEXTURE,
               DrawMode.QUADS,
               256,
               MultiPhaseParameters.builder()
                  .program(BEACON_BEAM_PROGRAM)
                  .writeMaskState(ALL_MASK)
                  .transparency(TRANSLUCENT_TRANSPARENCY)
                  .target(GLOW_TARGET)
                  .texture(new Texture(id, false, false))
                  .build(false)
            )
      );
      private static final Identifier WHITE_TEXTURE = new Identifier("misc/white.png");
      private static final RenderLayer DEFAULT_GLOW_LAYER = GLOW_LAYER_FUNC.apply(WHITE_TEXTURE);

      private GlowRenderTypes(String string, VertexFormat vertexFormat, DrawMode mode, int i, boolean bl, boolean bl2, Runnable runnable, Runnable runnable2) {
         super(string, vertexFormat, mode, i, bl, bl2, runnable, runnable2);
      }

      public static RenderLayer getRenderType(RenderLayer base) {
         return RenderLayerHelper.copy(base, "elysium:glow", builder -> builder.target(GLOW_TARGET));
      }

      public static RenderLayer unwrap(RenderLayer base) {
         return RenderLayerHelper.copy(base, "elysium:unglowed", builder -> {});
      }
   }
}
