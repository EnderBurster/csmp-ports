package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.accessors;

import java.util.List;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.block.entity.BeaconBlockEntity.BeamSegment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BeaconBlockEntity.class})
public interface BeaconBlockEntityAccessor {
   @Accessor("level")
   int getLevel();

   @Accessor("primary")
   StatusEffect getPrimary();

   @Accessor("secondary")
   StatusEffect getSecondary();

   @Accessor("field_19178")
   List<BeamSegment> getField();

   @Accessor("minY")
   void setMinY(int var1);

   @Accessor("level")
   void setLevel(int var1);

   @Accessor("beamSegments")
   void setBeamSegments(List<BeamSegment> var1);
}
