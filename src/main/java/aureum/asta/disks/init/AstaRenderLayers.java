package aureum.asta.disks.init;

import aureum.asta.disks.api.lodestone.handlers.RenderHandler;
import aureum.asta.disks.api.lodestone.setup.LodestoneShaders;
import aureum.asta.disks.api.lodestone.systems.rendering.Phases;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class AstaRenderLayers extends RenderPhase {
    public static final HashMap<Pair<Integer, RenderLayer>, RenderLayer> COPIES = new HashMap<>();
    public static final RenderLayer TEST = createGenericRenderLayer(
            "aureum-asta-disks",
            "test",
            VertexFormats.POSITION_TEXTURE_COLOR_LIGHT,
            VertexFormat.DrawMode.QUADS,
            AstaShaders.BORDER_LINEAR.phase,
            Phases.ADDITIVE_TRANSPARENCY,
            RenderPhase.BLOCK_ATLAS_TEXTURE
    );

    public static void init(){};

    public AstaRenderLayers(String name, Runnable beginAction, Runnable endAction) {
        super(name, beginAction, endAction);
    }

    public static RenderLayer createGenericRenderLayer(
            String modId, String name, VertexFormat format, VertexFormat.DrawMode mode, RenderPhase.ShaderProgram shader, RenderPhase.Transparency transparency, Identifier texture
    ) {
        return createGenericRenderLayer(modId + ":" + name, format, mode, shader, transparency, new RenderPhase.Texture(texture, false, false));
    }

    public static RenderLayer createGenericRenderLayer(
            String modId, String name, VertexFormat format, VertexFormat.DrawMode mode, RenderPhase.ShaderProgram shader, RenderPhase.Transparency transparency, RenderPhase.TextureBase texture
    ) {
        return createGenericRenderLayer(modId + ":" + name, format, mode, shader, transparency, texture);
    }

    public static RenderLayer createGenericRenderLayer(String modId, String name, VertexFormat format, VertexFormat.DrawMode mode, RenderPhase.ShaderProgram shader, RenderPhase.Transparency transparency) {
        return createGenericRenderLayer(modId + ":" + name, format, mode, shader, transparency, RenderPhase.NO_TEXTURE);
    }


    public static RenderLayer createGenericRenderLayer(
            String name, VertexFormat format, VertexFormat.DrawMode mode, RenderPhase.ShaderProgram shader, RenderPhase.Transparency transparency, RenderPhase.TextureBase texture
    ) {
        RenderLayer type = RenderLayer.of(
                name,
                format,
                mode,
                FabricLoader.getInstance().isModLoaded("sodium") ? 2097152 : 256,
                false,
                false,
                RenderLayer.MultiPhaseParameters.builder()
                        .program(shader)
                        .writeMaskState(new RenderPhase.WriteMaskState(true, true))
                        .lightmap(new RenderPhase.Lightmap(false))
                        .transparency(transparency)
                        .texture(texture)
                        .cull(new RenderPhase.Cull(true))
                        .build(true)
        );
        RenderHandler.addRenderLayer(type);
        return type;
    }
}
