package aureum.asta.disks.ports.amarite.mialib.templates;

import java.util.List;
import java.util.Set;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public interface BlankMixinPlugin extends IMixinConfigPlugin {
   default void onLoad(String mixinPackage) {
   }

   default String getRefMapperConfig() {
      return null;
   }

   default boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
      return true;
   }

   default void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
   }

   default List<String> getMixins() {
      return null;
   }

   default void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }

   default void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
   }
}
