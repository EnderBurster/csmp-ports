package aureum.asta.disks.ports.charter.client.shaders;

import aureum.asta.disks.api.lodestone.systems.rendering.ExtendedShader;
import aureum.asta.disks.api.lodestone.systems.rendering.ShaderHolder;
import aureum.asta.disks.ports.charter.Charter;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.resource.ResourceFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CharterShaders {
    public static List<Pair<ShaderProgram, Consumer<ShaderProgram>>> shaderList;

    public static ShaderHolder BORDER_RADIAL = new ShaderHolder();
    public static ShaderHolder BORDER_LINEAR = new ShaderHolder();
    public static ShaderHolder BORDER_BOTTOM = new ShaderHolder();

    public static void init(ResourceFactory manager) throws IOException {
        shaderList = new ArrayList<>();
        registerShader(ExtendedShader.createShaderInstance(BORDER_RADIAL, manager, Charter.id("border/border_radial"), VertexFormats.POSITION_COLOR_TEXTURE));
        registerShader(ExtendedShader.createShaderInstance(BORDER_LINEAR, manager, Charter.id("border/border_linear"), VertexFormats.POSITION_COLOR_TEXTURE));
        registerShader(ExtendedShader.createShaderInstance(BORDER_BOTTOM, manager, Charter.id("border/border_bottom"), VertexFormats.POSITION_COLOR_TEXTURE));
    }

    public static void registerShader(ExtendedShader extendedShaderInstance) {
        registerShader(extendedShaderInstance, shader -> ((ExtendedShader)shader).getHolder().setInstance((ExtendedShader)shader));
    }

    public static void registerShader(ShaderProgram shader, Consumer<ShaderProgram> onLoaded) {
        shaderList.add(Pair.of(shader, onLoaded));
    }
}