package aureum.asta.disks.api.lodestone.systems.postprocess;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.LodestoneLib;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import net.minecraft.util.Identifier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlImportProcessor;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

public class LodestoneGlslPreprocessor extends GlImportProcessor {
   @Nullable
   public String loadImport(boolean inline, String name) {
      LodestoneLib.LOGGER.debug("Loading moj_import in EffectProgram: " + name);
      Identifier id = new Identifier(name);
      Identifier id1 = new Identifier(id.getNamespace(), "shaders/include/" + id.getPath() + ".glsl");

      try {
         String s2;
         try (InputStream resource1 = MinecraftClient.getInstance().getResourceManager().getResourceOrThrow(id1).getInputStream()) {
            s2 = IOUtils.toString(resource1, StandardCharsets.UTF_8);
         }

         return s2;
      } catch (IOException var11) {
         LodestoneLib.LOGGER.error("Could not open GLSL import {}: {}", name, var11.getMessage());
         return "#error " + var11.getMessage();
      }
   }
}
