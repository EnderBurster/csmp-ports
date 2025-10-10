package aureum.asta.disks.ports.amarite.amarite.util;

import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.mialib.templates.BlankMixinPlugin;

public class MixinPlugin implements BlankMixinPlugin {
   @Override
   public boolean shouldApplyMixin(String targetClassName, @NotNull String mixinClassName) {
      return mixinClassName.contains("$") ? FabricLoader.getInstance().isModLoaded(mixinClassName.split("\\$")[0].toLowerCase()) : true;
   }
}
