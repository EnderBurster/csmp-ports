package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.client.model.Existent;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.CompositeEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({CompositeEntityModel.class})
public abstract class CompositeEntityModelMixin implements Existent {
   @Shadow
   public abstract Iterable<ModelPart> getParts();

   @Override
   public List<ModelPart> getModelParts() {
      return Lists.newLinkedList(this.getParts());
   }
}
