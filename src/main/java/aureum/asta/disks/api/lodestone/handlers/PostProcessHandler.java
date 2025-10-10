package aureum.asta.disks.api.lodestone.handlers;

import aureum.asta.disks.api.lodestone.systems.postprocess.PostProcessor;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.util.math.MatrixStack;

public class PostProcessHandler {
   private static final List<PostProcessor> instances = new ArrayList<>();
   private static boolean didCopyDepth = false;

   public static void addInstance(PostProcessor instance) {
      instances.add(instance);
   }

   public static void copyDepthBuffer() {
      if (!didCopyDepth) {
         instances.forEach(PostProcessor::copyDepthBuffer);
         didCopyDepth = true;
      }
   }

   public static void resize(int width, int height) {
      instances.forEach(i -> i.resize(width, height));
   }

   public static void renderLast(MatrixStack matrices) {
      copyDepthBuffer();
      PostProcessor.viewModelStack = matrices;
      instances.forEach(PostProcessor::applyPostProcess);
      didCopyDepth = false;
   }
}
