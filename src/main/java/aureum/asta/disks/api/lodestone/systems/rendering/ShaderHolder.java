package aureum.asta.disks.api.lodestone.systems.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.gl.ShaderProgram;

public class ShaderHolder {
   public ExtendedShader instance;
   public ArrayList<String> uniforms;
   public ArrayList<UniformData> defaultUniformData = new ArrayList<>();
   public final net.minecraft.client.render.RenderPhase.ShaderProgram phase = new net.minecraft.client.render.RenderPhase.ShaderProgram(this.getInstance());

   public ShaderHolder(String... uniforms) {
      this.uniforms = new ArrayList<>(List.of(uniforms));
   }

   public void setUniformDefaults() {
      RenderSystem.setShaderTexture(1, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
      this.defaultUniformData.forEach(u -> u.setUniformValue(this.instance.getUniformOrDefault(u.uniformName)));
   }

   public void setInstance(ExtendedShader instance) {
      this.instance = instance;
   }

   public Supplier<ShaderProgram> getInstance() {
      return () -> this.instance;
   }
}
