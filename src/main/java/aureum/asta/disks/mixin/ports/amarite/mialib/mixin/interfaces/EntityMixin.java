package aureum.asta.disks.mixin.ports.amarite.mialib.mixin.interfaces;

import aureum.asta.disks.ports.amarite.mialib.interfaces.MEntity;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({Entity.class})
public class EntityMixin implements MEntity {
}
