package aureum.asta.disks.ports.charter.common.entity;

import aureum.asta.disks.api.lodestone.handlers.ScreenshakeHandler;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.api.lodestone.systems.screenshake.PositionedScreenshakeInstance;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.damage.CharterDamageSources;
import aureum.asta.disks.ports.charter.common.init.CharterEntities;
import aureum.asta.disks.ports.charter.common.init.CharterParticles;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class BrokenDivinityEntity extends LesserDivinityEntity{
    public BrokenDivinityEntity(EntityType<?> type, World world) {
        super(type, world);
        this.broken = true;
    }

    public BrokenDivinityEntity(World world) {
        super(CharterEntities.BROKEN_DIVINITY_ENTITY, world);
        this.broken = true;
    }

    @Override
    protected void scatterShards() {
    }

    @Override
    public void attemptBan(Entity entity, MinecraftServer server, int minutes, String reason) {
        if(entity == null || this.shardsScattered) return;
        entity.stopRiding();
        entity.addVelocity(100f, 100f, 100f);
        entity.velocityDirty = true;

        if(!(entity instanceof PlayerEntity)) return;

        entity.getComponent(CharterComponents.PLAYER_COMPONENT).divinityFlying = true;
        entity.getComponent(CharterComponents.PLAYER_COMPONENT).tantalus = true;
    }

    @Override
    protected void timedEffects() {
        if(this.age == 25)
        {
            attemptBan(this.getFirstPassenger(), this.getServer(), -1, "Your existence was repurposed.");
            ScreenshakeHandler.addScreenshake(new PositionedScreenshakeInstance(130, this.getPos(), 5, 1, 40, Easing.QUINTIC_OUT).setIntensity(1f, 0.0f).setEasing(Easing.BOUNCE_IN_OUT));
        }

        if(this.age >= 25 && this.age <= 100)
        {
            for (int i = 0; i < 6; i++)
            {
                ParticleBuilders.create(CharterParticles.DIVINITY_SMOKE)
                        .setLifetime(10 + this.random.nextInt(5))
                        .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                        .setAlphaEasing(Easing.LINEAR)
                        .setColorCoefficient(2.0f)
                        .setColorEasing(Easing.ELASTIC_OUT)
                        .setSpinEasing(Easing.SINE_IN)
                        .setScaleEasing(Easing.SINE_IN_OUT)
                        .setColor(0.98f, 0.973f, 0.698f)
                        .setAlpha(0.1f, 0.0f)
                        .setSpinOffset(this.random.nextFloat())
                        .setScale(5f + this.random.nextFloat()*3f, 2f + this.random.nextFloat(), 5f + this.random.nextFloat()*3)
                        .randomOffset(5.0f, 5.0f)
                        .spawn(this.world, this.getX(), this.getY() + 1f, this.getZ());
            }

            if(!this.lastUse) return;

            this.getWorld().createExplosion(this, this.getX() + (random.nextFloat()-0.5f) * 25, this.getY() + (random.nextFloat()-0.5f) * 2f, this.getZ() + (random.nextFloat()-0.5f) * 25, 4.0f, true, World.ExplosionSourceType.MOB);
        }
    }
}
