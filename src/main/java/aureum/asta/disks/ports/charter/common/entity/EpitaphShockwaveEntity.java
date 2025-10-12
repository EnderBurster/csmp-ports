package aureum.asta.disks.ports.charter.common.entity;

import aureum.asta.disks.api.lodestone.handlers.ScreenshakeHandler;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.api.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import aureum.asta.disks.ports.charter.common.damage.CharterDamageSources;
import aureum.asta.disks.ports.charter.common.init.CharterEntities;
import aureum.asta.disks.ports.charter.common.init.CharterParticles;
import aureum.asta.disks.ports.charter.common.util.CharterUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

public class EpitaphShockwaveEntity extends Entity {
    private PlayerEntity bannedPlayer = null;
    public static final int ACTIVATION_AGE = 112;
    public static final int MAX_AGE = 175;

    public EpitaphShockwaveEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public EpitaphShockwaveEntity(World world) {
        super(CharterEntities.SHOCKWAVE_ENTITY, world);
    }

    @Override
    protected void initDataTracker() {

    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {

    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {

    }

    public void setBannedPlayer(@Nullable PlayerEntity bannedPlayer) {
        this.bannedPlayer = bannedPlayer;
    }

    public PlayerEntity getBannedPlayer() {
        return this.bannedPlayer;
    }

    @Override
    public void tick() {
        super.tick();

        if(this.age == ACTIVATION_AGE)
        {
            ScreenshakeHandler.addScreenshake(new PositionedScreenshakeInstance(80, this.getPos(), 40, 1, 100, Easing.QUINTIC_OUT).setIntensity(2f, 0.0f).setEasing(Easing.BOUNCE_IN_OUT));

            if(this.bannedPlayer != null) {
                this.bannedPlayer.damage(this.bannedPlayer.getDamageSources().create(CharterDamageSources.EPITAPH_BAN), 2000f);
            }

            int count = 400;
            float goldenAngle = (float) (Math.PI * (3 - Math.sqrt(5))); // ~2.39996

            if(this.world.isClient)
            {
                for (int i = 0; i < count; i++) {
                    double u = Math.random();
                    double v = Math.random();

                    double theta = 2 * Math.PI * u;
                    double phi = Math.acos(2 * v - 1);
                    double sinPhi = Math.sin(phi);

                    double xDir = sinPhi * Math.cos(theta);
                    double yDir = Math.cos(phi);
                    double zDir = sinPhi * Math.sin(theta);

                    Vector3f direction = new Vector3f((float) xDir, (float) yDir, (float) zDir);
                    Vector3f motion = direction.mul(1);

                    ParticleBuilders.create(CharterParticles.EPITAPH_PARTICLE)
                            .setLifetime(120)
                            .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                            .setAlphaEasing(Easing.LINEAR)
                            .setColorEasing(Easing.ELASTIC_OUT)
                            .setSpinEasing(Easing.SINE_IN)
                            .setScaleEasing(Easing.SINE_IN_OUT)
                            .setAlpha(1f, 0.0f)
                            .setSpinOffset(this.world.random.nextFloat())
                            .setScale(0.6f, 1.0f)
                            .setGravity(0)
                            .setForcedMotion(motion, motion)
                            .setMotionCoefficient(0.0f)
                            .enableNoClip()
                            .spawn(this.world, this.getX(), this.getY(), this.getZ());
                }
            }
        }
        else if(this.age >= MAX_AGE)
        {
            CharterUtils.attemptBan(this.bannedPlayer, this.getServer(), -1, "Your existence was repurposed.");
            this.remove(RemovalReason.DISCARDED);
        }
    }

    public boolean hasNoGravity() {
        return true;
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }
}
