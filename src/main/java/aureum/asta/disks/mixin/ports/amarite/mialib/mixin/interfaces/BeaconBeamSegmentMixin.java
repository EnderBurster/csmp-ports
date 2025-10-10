package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.interfaces;

import net.minecraft.block.entity.BeaconBlockEntity.BeamSegment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import aureum.asta.disks.ports.amarite.mialib.interfaces.MBeaconBeamSegment;

@Mixin({BeamSegment.class})
public class BeaconBeamSegmentMixin implements MBeaconBeamSegment {
   @Unique
   private boolean hidden = false;

   @Override
   public boolean mialib$isHidden() {
      return this.hidden;
   }

   @Override
   public void mialib$setHidden(boolean hidden) {
      this.hidden = hidden;
   }
}
