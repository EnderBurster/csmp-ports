package aureum.asta.disks.api.lodestone.systems.rendering;

import java.util.ArrayList;
import net.minecraft.util.math.Vec3d;

public interface PositionTrackedEntity {
   void trackPastPositions();

   ArrayList<Vec3d> getPastPositions();
}
