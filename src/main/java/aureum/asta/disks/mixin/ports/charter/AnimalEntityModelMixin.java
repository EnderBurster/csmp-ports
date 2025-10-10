package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.client.model.Existent;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({AnimalModel.class})
public abstract class AnimalEntityModelMixin implements Existent {
   @Shadow
   protected abstract Iterable<ModelPart> getBodyParts();

   @Shadow
   protected abstract Iterable<ModelPart> getHeadParts();

   @Override
   public List<ModelPart> getModelParts() {
      return Stream.of(Lists.newLinkedList(this.getHeadParts()), Lists.newLinkedList(this.getBodyParts()))
         .flatMap(Collection::stream)
         .collect(Collectors.toList());
   }
}
