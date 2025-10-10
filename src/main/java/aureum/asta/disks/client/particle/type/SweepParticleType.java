package aureum.asta.disks.particle.type;

import aureum.asta.disks.particle.contract.ColoredParticleInitialData;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;

public class SweepParticleType extends DefaultParticleType {
    public ColoredParticleInitialData initialData;

    public SweepParticleType(boolean alwaysShow) {super(alwaysShow);}

    public ParticleEffect setData(ColoredParticleInitialData target)
    {
        this.initialData = target;
        return this;
    }

}
