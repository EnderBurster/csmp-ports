package aureum.asta.disks.ports.elysium.machine;

import java.util.Optional;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public interface BeamPowered {
   @Nullable
   BlockPos getBeamSourcePos();

   void setBeamSourcePos(@Nullable BlockPos var1);

   boolean canAcceptBeam(Direction var1);

   default int getBeamPower(WorldAccess level, BlockPos pos) {
      BlockPos beaconPos = this.getBeamSourcePos();
      if (beaconPos != null) {
         if (level.getBlockEntity(beaconPos, BlockEntityType.BEACON).filter(t -> !t.getBeamSegments().isEmpty()).isPresent()) {
            return 4;
         }

         Optional<Integer> power = level.getBlockState(beaconPos).getOrEmpty(ElysiumMachines.ELYSIUM_POWER);
         if (power.isPresent()) {
            return power.get();
         }
      }

      return 0;
   }
}
