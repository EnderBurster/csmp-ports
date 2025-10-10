package aureum.asta.disks.ports.amarite.amarite.cca;

import aureum.asta.disks.ports.amarite.amarite.entities.DiscEntity;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

public class DiscPylonComponent implements AutoSyncedComponent, CommonTickingComponent {
   public static final int PYLON_MAX_DURATION = 160;
   private final Entity entity;
   public int pylonCharge = 0;

   public DiscPylonComponent(Entity entity) {
      this.entity = entity;
   }

   public void tick() {
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.pylonCharge = tag.getInt("PylonCharge");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      tag.putInt("PylonCharge", this.pylonCharge);
   }
}
