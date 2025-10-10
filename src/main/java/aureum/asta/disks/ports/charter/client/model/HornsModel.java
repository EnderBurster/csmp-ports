package aureum.asta.disks.ports.charter.client.model;

import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public abstract class HornsModel extends Model {
   public HornsModel(Function<Identifier, RenderLayer> function) {
      super(function);
   }

   public abstract ModelPart getHorns();

   public abstract Identifier getTexture();

   public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
      this.getHorns().render(matrices, vertices, light, overlay, red, green, blue, alpha);
   }
}
