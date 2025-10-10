package aureum.asta.disks.api.lodestone.setup;

import com.mojang.datafixers.util.Pair;
import aureum.asta.disks.api.lodestone.handlers.RenderHandler;
import aureum.asta.disks.api.lodestone.systems.rendering.Phases;
import aureum.asta.disks.api.lodestone.systems.rendering.ShaderUniformHandler;
import java.util.HashMap;
import java.util.function.Function;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Util;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.RenderLayer.MultiPhase;
import net.minecraft.client.render.RenderLayer.MultiPhaseParameters;
import net.minecraft.client.render.VertexFormat.DrawMode;
import net.minecraft.client.render.RenderPhase.Cull;
import net.minecraft.client.render.RenderPhase.Lightmap;
import net.minecraft.client.render.RenderPhase.Texture;
import net.minecraft.client.render.RenderPhase.Transparency;
import net.minecraft.client.render.RenderPhase.WriteMaskState;
import net.minecraft.client.render.RenderPhase.TextureBase;
import net.minecraft.client.render.RenderPhase.ShaderProgram;

public class LodestoneRenderLayers extends RenderPhase {
   public static final HashMap<Pair<Integer, RenderLayer>, RenderLayer> COPIES = new HashMap<>();
   public static final RenderLayer ADDITIVE_PARTICLE = createGenericRenderLayer(
      "aureum-asta-disks",
      "additive_particle",
      VertexFormats.POSITION_TEXTURE_COLOR_LIGHT,
      DrawMode.QUADS,
      LodestoneShaders.LODESTONE_PARTICLE.phase,
      Phases.ADDITIVE_TRANSPARENCY,
      SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE
   );

   public static final RenderLayer ADDITIVE_BLOCK = createGenericRenderLayer(
      "aureum-asta-disks",
      "block",
      VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
      DrawMode.QUADS,
      LodestoneShaders.ADDITIVE_TEXTURE.phase,
      Phases.ADDITIVE_TRANSPARENCY,
      SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE
   );
   public static final RenderLayer ADDITIVE_SOLID = createGenericRenderLayer(
      "aureum-asta-disks", "additive_solid", VertexFormats.POSITION_COLOR_LIGHT, DrawMode.QUADS, RenderPhase.POSITION_COLOR_LIGHTMAP_PROGRAM, Phases.ADDITIVE_TRANSPARENCY
   );
   public static final RenderLayer TRANSPARENT_PARTICLE = createGenericRenderLayer(
      "aureum-asta-disks",
      "transparent_particle",
      VertexFormats.POSITION_TEXTURE_COLOR_LIGHT,
      DrawMode.QUADS,
      LodestoneShaders.LODESTONE_PARTICLE.phase,
      Phases.NORMAL_TRANSPARENCY,
      SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE
   );
   public static final RenderLayer TRANSPARENT_BLOCK = createGenericRenderLayer(
      "aureum-asta-disks",
      "transparent_block",
      VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
      DrawMode.QUADS,
      RenderPhase.POSITION_COLOR_LIGHTMAP_PROGRAM,
      Phases.NORMAL_TRANSPARENCY,
      SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE
   );
   public static final RenderLayer TRANSPARENT_SOLID = createGenericRenderLayer(
      "aureum-asta-disks", "transparent_solid", VertexFormats.POSITION_COLOR_LIGHT, DrawMode.QUADS, RenderPhase.POSITION_COLOR_LIGHTMAP_PROGRAM, Phases.NORMAL_TRANSPARENCY
   );
   public static final RenderLayer OUTLINE_SOLID = createGenericRenderLayer(
      "aureum-asta-disks",
      "outline_solid",
      VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
      DrawMode.QUADS,
      LodestoneShaders.ADDITIVE_TEXTURE.phase,
      Phases.ADDITIVE_TRANSPARENCY,
      SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE
   );
   public static final RenderLayer VERTEX_DISTORTION = createGenericRenderLayer(
      "aureum-asta-disks",
      "vertex_distortion",
      VertexFormats.POSITION_TEXTURE_COLOR_LIGHT,
      DrawMode.QUADS,
      LodestoneShaders.VERTEX_DISTORTION.phase,
      Phases.ADDITIVE_TRANSPARENCY,
      SpriteAtlasTexture.PARTICLE_ATLAS_TEXTURE
   );
   public static final RenderLayerProvider TEXTURE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(), "texture", VertexFormats.POSITION_COLOR_TEXTURE_LIGHT, DrawMode.QUADS, RenderPhase.POSITION_COLOR_LIGHTMAP_PROGRAM, Phases.NO_TRANSPARENCY, texture
         )
   );
   public static final RenderLayerProvider TRANSPARENT_TEXTURE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(),
            "transparent_texture",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            RenderPhase.POSITION_COLOR_TEXTURE_LIGHTMAP_PROGRAM,
            Phases.NORMAL_TRANSPARENCY,
            texture
         )
   );
   public static final RenderLayerProvider TRANSPARENT_TEXTURE_TRIANGLE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(),
            "transparent_texture_triangle",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            LodestoneShaders.TRIANGLE_TEXTURE.phase,
            Phases.NORMAL_TRANSPARENCY,
            texture
         )
   );
   public static final RenderLayerProvider ADDITIVE_TEXTURE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(),
            "additive_texture",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            LodestoneShaders.ADDITIVE_TEXTURE.phase,
            Phases.ADDITIVE_TRANSPARENCY,
            texture
         )
   );
   public static final RenderLayerProvider ADDITIVE_TEXTURE_TRIANGLE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(),
            "additive_texture_triangle",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            LodestoneShaders.TRIANGLE_TEXTURE.phase,
            Phases.ADDITIVE_TRANSPARENCY,
            texture
         )
   );
   public static final RenderLayerProvider VERTEX_DISTORTION_TEXTURE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(),
            "vertex_distortion_texture",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            LodestoneShaders.VERTEX_DISTORTION.phase,
            Phases.ADDITIVE_TRANSPARENCY,
            texture
         )
   );
   public static final RenderLayerProvider RADIAL_NOISE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(),
            "radial_noise",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            LodestoneShaders.RADIAL_NOISE.phase,
            Phases.ADDITIVE_TRANSPARENCY,
            texture
         )
   );
   public static final RenderLayerProvider RADIAL_SCATTER_NOISE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(),
            "radial_scatter_noise",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            LodestoneShaders.RADIAL_SCATTER_NOISE.phase,
            Phases.ADDITIVE_TRANSPARENCY,
            texture
         )
   );
   public static final RenderLayerProvider SCROLLING_TEXTURE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(),
            "scrolling_texture",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            LodestoneShaders.SCROLLING_TEXTURE.phase,
            Phases.ADDITIVE_TRANSPARENCY,
            texture
         )
   );
   public static final RenderLayerProvider SCROLLING_TEXTURE_TRIANGLE = new RenderLayerProvider(
      texture -> createGenericRenderLayer(
            texture.getNamespace(),
            "scrolling_texture_triangle",
            VertexFormats.POSITION_COLOR_TEXTURE_LIGHT,
            DrawMode.QUADS,
            LodestoneShaders.SCROLLING_TRIANGLE_TEXTURE.phase,
            Phases.ADDITIVE_TRANSPARENCY,
            texture
         )
   );
   public static final Function<RenderLayerData, RenderLayer> GENERIC = data -> createGenericRenderLayer(
         data.name, data.format, data.mode, data.shader, data.transparency, data.texture
      );

   public LodestoneRenderLayers(String string, Runnable runnable, Runnable runnable2) {
      super(string, runnable, runnable2);
   }

   public static void yea() {
   }

   public static RenderLayer createGenericRenderLayer(
      String modId, String name, VertexFormat format, DrawMode mode, ShaderProgram shader, Transparency transparency, Identifier texture
   ) {
      return createGenericRenderLayer(modId + ":" + name, format, mode, shader, transparency, new Texture(texture, false, false));
   }

   public static RenderLayer createGenericRenderLayer(
      String modId, String name, VertexFormat format, DrawMode mode, ShaderProgram shader, Transparency transparency, TextureBase texture
   ) {
      return createGenericRenderLayer(modId + ":" + name, format, mode, shader, transparency, texture);
   }

   public static RenderLayer createGenericRenderLayer(String modId, String name, VertexFormat format, DrawMode mode, ShaderProgram shader, Transparency transparency) {
      return createGenericRenderLayer(modId + ":" + name, format, mode, shader, transparency, RenderPhase.NO_TEXTURE);
   }


   public static RenderLayer createGenericRenderLayer(
      String name, VertexFormat format, DrawMode mode, ShaderProgram shader, Transparency transparency, TextureBase texture
   ) {
      RenderLayer type = RenderLayer.of(
         name,
         format,
         mode,
         FabricLoader.getInstance().isModLoaded("sodium") ? 2097152 : 256,
         false,
         false,
         MultiPhaseParameters.builder()
            .program(shader)
            .writeMaskState(new WriteMaskState(true, true))
            .lightmap(new Lightmap(false))
            .transparency(transparency)
            .texture(texture)
            .cull(new Cull(true))
            .build(true)
      );
      RenderHandler.addRenderLayer(type);
      return type;
   }

   public static RenderLayer queueUniformChanges(RenderLayer type, ShaderUniformHandler handler) {
      RenderHandler.HANDLERS.put(type, handler);
      return type;
   }

   public static RenderLayer copy(int index, RenderLayer type) {
      return COPIES.computeIfAbsent(Pair.of(index, type), p -> GENERIC.apply(new RenderLayerData((MultiPhase)type)));
   }

   public static RenderLayer getOutlineTranslucent(Identifier texture, boolean cull) {
      return RenderLayer.of(
         "lodestone:outline_translucent",
         VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
         DrawMode.QUADS,
         FabricLoader.getInstance().isModLoaded("sodium") ? 262144 : 256,
         false,
         true,
         MultiPhaseParameters.builder()
            .program(cull ? ENTITY_TRANSLUCENT_CULL_PROGRAM : ENTITY_TRANSLUCENT_PROGRAM)
            .texture(new Texture(texture, false, false))
            .transparency(TRANSLUCENT_TRANSPARENCY)
            .cull(cull ? ENABLE_CULLING : DISABLE_CULLING)
            .lightmap(ENABLE_LIGHTMAP)
            .overlay(ENABLE_OVERLAY_COLOR)
            .writeMaskState(COLOR_MASK)
            .build(false)
      );
   }

   public static class RenderLayerData {
      public final String name;
      public final VertexFormat format;
      public final DrawMode mode;
      public final ShaderProgram shader;
      public Transparency transparency = Phases.ADDITIVE_TRANSPARENCY;
      public final TextureBase texture;

      public RenderLayerData(String name, VertexFormat format, DrawMode mode, ShaderProgram shader, TextureBase texture) {
         this.name = name;
         this.format = format;
         this.mode = mode;
         this.shader = shader;
         this.texture = texture;
      }

      public RenderLayerData(String name, VertexFormat format, DrawMode mode, ShaderProgram shader, Transparency transparency, TextureBase texture) {
         this(name, format, mode, shader, texture);
         this.transparency = transparency;
      }

      public RenderLayerData(MultiPhase type) {
         this(
            type.name,
            type.getVertexFormat(),
            type.getDrawMode(),
            type.phases.program,
            type.phases.transparency,
            type.phases.texture
         );
      }
   }

   public static class RenderLayerProvider {
      private final Function<Identifier, RenderLayer> function;
      private final Function<Identifier, RenderLayer> memorizedFunction;

      public RenderLayerProvider(Function<Identifier, RenderLayer> function) {
         this.function = function;
         this.memorizedFunction = Util.memoize(function);
      }

      public RenderLayer apply(Identifier texture) {
         return this.function.apply(texture);
      }

      public RenderLayer applyAndCache(Identifier texture) {
         return this.memorizedFunction.apply(texture);
      }
   }
}
