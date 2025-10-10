package aureum.asta.disks.client.sound;

import aureum.asta.disks.cca.entity.ThunderstruckComponent;
import aureum.asta.disks.init.AstaEntityComponents;
import aureum.asta.disks.sound.AstaSounds;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.random.Random;

public class SparkSoundInstance extends MovingSoundInstance {
    private final Entity entity;

    public SparkSoundInstance(Entity entity) {
        super(AstaSounds.ENTITY_GENERIC_SPARK, entity.getSoundCategory(), Random.create());
        this.entity = entity;
        x = entity.getX();
        y = entity.getY();
        z = entity.getZ();
        repeat = true;
        repeatDelay = 0;
    }

    @Override
    public void tick() {
        boolean done = entity == null || !entity.isAlive();
        if (!done) {
            ThunderstruckComponent lightningDashComponent = AstaEntityComponents.THUNDERSTRUCK.getNullable(entity);
            if (lightningDashComponent == null || !lightningDashComponent.isFloating()) {
                done = true;
            }
        }
        if (done) {
            volume -= 0.1F;
            if (volume < 0) {
                setDone();
            }
            return;
        }
        x = entity.getX();
        y = entity.getY();
        z = entity.getZ();
    }
}
