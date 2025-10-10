package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.client.model.Existent;
import java.util.List;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.EntityModel;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({EntityModel.class})
public abstract class EntityModelMixin implements Existent {
   @Override
   public List<ModelPart> getModelParts() {
      return List.of();
   }
}
