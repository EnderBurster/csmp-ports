package aureum.asta.disks.api.lodestone.systems.rendering.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import aureum.asta.disks.api.lodestone.handlers.RenderHandler;
import aureum.asta.disks.api.lodestone.setup.LodestoneShaders;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.client.render.VertexFormat.DrawMode;

public class ParticleTextureSheets {
   public static final ParticleTextureSheet ADDITIVE = new ParticleTextureSheet() {
      public void begin(BufferBuilder builder, TextureManager manager) {
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 1);
         RenderSystem.setShader(LodestoneShaders.LODESTONE_PARTICLE.getInstance());
         RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
         RenderHandler.PARTICLE_MATRIX = RenderSystem.getModelViewMatrix();
         builder.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
      }

      public void draw(Tessellator tesselator) {
         tesselator.draw();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }
   };
   public static final ParticleTextureSheet TRANSPARENT = new ParticleTextureSheet() {
      public void begin(BufferBuilder builder, TextureManager manager) {
         RenderSystem.depthMask(false);
         RenderSystem.enableBlend();
         RenderSystem.blendFunc(770, 771);
         RenderSystem.setShaderTexture(0, SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE);
         RenderHandler.PARTICLE_MATRIX = RenderSystem.getModelViewMatrix();
         builder.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR_LIGHT);
      }

      public void draw(Tessellator tesselator) {
         tesselator.draw();
         RenderSystem.depthMask(true);
         RenderSystem.disableBlend();
         RenderSystem.defaultBlendFunc();
      }
   };
}
