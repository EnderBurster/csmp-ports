package aureum.asta.disks.api.lodestone.systems.postprocess;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import aureum.asta.disks.api.lodestone.LodestoneLib;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.JsonEffectShaderProgram;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Vector3fc;

public abstract class PostProcessor {
   protected static final MinecraftClient MC = MinecraftClient.getInstance();
   public static final Collection<Pair<String, Consumer<GlUniform>>> COMMON_UNIFORMS = Lists.newArrayList(
      new Pair[]{
         Pair.of("cameraPos", (Consumer<GlUniform>)u -> u.set(new Vector3f((float) MC.gameRenderer.getCamera().getPos().getX(), (float) MC.gameRenderer.getCamera().getPos().getY(), (float) MC.gameRenderer.getCamera().getPos().getZ()))),
         Pair.of("lookVector", (Consumer<GlUniform>)u -> u.set(MC.gameRenderer.getCamera().getHorizontalPlane())),
         Pair.of("upVector", (Consumer<GlUniform>)u -> u.set(MC.gameRenderer.getCamera().getVerticalPlane())),
         Pair.of("leftVector", (Consumer<GlUniform>)u -> u.set(MC.gameRenderer.getCamera().getDiagonalPlane())),
         Pair.of("invViewMat", (Consumer<GlUniform>)u -> {
            Matrix4f invertedViewMatrix = new Matrix4f(PostProcessor.viewModelStack.peek().getPositionMatrix());
            invertedViewMatrix.invert();
            u.set(invertedViewMatrix);
         }),
         Pair.of("invProjMat", (Consumer<GlUniform>)u -> {
            Matrix4f invertedProjectionMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
            invertedProjectionMatrix.invert();
            u.set(invertedProjectionMatrix);
         }),
         Pair.of("nearPlaneDistance", (Consumer<GlUniform>)u -> u.set(0.05F)),
         Pair.of("farPlaneDistance", (Consumer<GlUniform>)u -> u.set(MC.gameRenderer.method_32796())),
         Pair.of(
            "fov",
            (Consumer<GlUniform>)u -> u.set((float)Math.toRadians(MC.gameRenderer.getFov(MC.gameRenderer.getCamera(), MC.getTickDelta(), true)))
         ),
         Pair.of("aspectRatio", (Consumer<GlUniform>)u -> u.set((float)MC.getWindow().getWidth() / (float)MC.getWindow().getHeight()))
      }
   );
   public static MatrixStack viewModelStack;
   private boolean initialized = false;
   protected PostEffectProcessor shaderEffect;
   protected JsonEffectShaderProgram[] effects;
   private Framebuffer tempDepthBuffer;
   private Collection<Pair<GlUniform, Consumer<GlUniform>>> defaultUniforms;
   private boolean isActive = true;
   protected double time;

   public abstract Identifier getShaderEffectId();

   public void init() {
      this.loadPostChain();
      if (this.shaderEffect != null) {
         this.tempDepthBuffer = this.shaderEffect.getSecondaryTarget("depthMain");
         this.defaultUniforms = new ArrayList<>();

         for (JsonEffectShaderProgram e : this.effects) {
            for (Pair<String, Consumer<GlUniform>> pair : COMMON_UNIFORMS) {
               GlUniform u = e.getUniformByName((String)pair.getFirst());
               if (u != null) {
                  this.defaultUniforms.add(Pair.of(u, (Consumer)pair.getSecond()));
               }
            }
         }
      }

      this.initialized = true;
   }

   public final void loadPostChain() {
      if (this.shaderEffect != null) {
         this.shaderEffect.close();
         this.shaderEffect = null;
      }

      try {
         Identifier file = this.getShaderEffectId();
         file = new Identifier(file.getNamespace(), "shaders/post/" + file.getPath() + ".json");
         this.shaderEffect = new PostEffectProcessor(MC.getTextureManager(), MC.getResourceManager(), MC.getFramebuffer(), file);
         this.shaderEffect.setupDimensions(MC.getWindow().getWidth(), MC.getWindow().getHeight());
         this.effects = this.shaderEffect.passes.stream().map(PostEffectPass::getProgram).toArray(JsonEffectShaderProgram[]::new);
      } catch (JsonParseException | IOException var2) {
         LodestoneLib.LOGGER.error("Failed to load post-processing shader: ", var2);
      }
   }

   public final void copyDepthBuffer() {
      if (this.isActive) {
         if (this.shaderEffect == null || this.tempDepthBuffer == null) {
            return;
         }

         this.tempDepthBuffer.copyDepthFrom(MC.getFramebuffer());
         GlStateManager._glBindFramebuffer(36009, MC.getFramebuffer().fbo);
      }
   }

   public void resize(int width, int height) {
      if (this.shaderEffect != null) {
         this.shaderEffect.setupDimensions(width, height);
         if (this.tempDepthBuffer != null) {
            this.tempDepthBuffer.resize(width, height, MinecraftClient.IS_SYSTEM_MAC);
         }
      }
   }

   private void applyDefaultUniforms() {
      Arrays.stream(this.effects).forEach(e -> e.getUniformByNameOrDummy("time").set((float)this.time));
      this.defaultUniforms.forEach(pair -> ((Consumer)pair.getSecond()).accept((GlUniform)pair.getFirst()));
   }

   public final void applyPostProcess() {
      if (this.isActive) {
         if (!this.initialized) {
            this.init();
         }

         if (this.shaderEffect != null) {
            this.time = this.time + (double)MC.getLastFrameDuration() / 20.0;
            this.applyDefaultUniforms();
            this.beforeProcess(viewModelStack);
            if (!this.isActive) {
               return;
            }

            this.shaderEffect.render(MC.getTickDelta());
            GlStateManager._glBindFramebuffer(36009, MC.getFramebuffer().fbo);
            this.afterProcess();
         }
      }
   }

   public abstract void beforeProcess(MatrixStack var1);

   public abstract void afterProcess();

   public final void setActive(boolean active) {
      this.isActive = active;
      if (!active) {
         this.time = 0.0;
      }
   }

   public final boolean isActive() {
      return this.isActive;
   }
}
