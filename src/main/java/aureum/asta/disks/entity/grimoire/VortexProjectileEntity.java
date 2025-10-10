package aureum.asta.disks.entity.grimoire;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.init.AstaEntities;
import aureum.asta.disks.sound.AstaSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class VortexProjectileEntity extends PersistentProjectileEntity {
    public int ticksUntilRemove = 5;
    public UUID owner = UUID.fromString("1ece513b-8d36-4f04-9be2-f341aa8c9ee2");
    public int maxLifespawn = 200;

    public VortexProjectileEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public VortexProjectileEntity(World world, LivingEntity owner) {
        super(AstaEntities.GRIMOIRE_VORTEX_PROJECTILE, owner, world);
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    public void tick()
    {
        super.tick();

        for (int v = 0; v < 5; v++)
        {
            ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE)
                    .setLifetime(6)
                    .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                    .setAlphaEasing(Easing.BOUNCE_IN_OUT)
                    .setColorCoefficient(2.0f)
                    .setColorEasing(Easing.ELASTIC_OUT)
                    .setSpinEasing(Easing.SINE_IN)
                    .setScaleEasing(Easing.SINE_IN_OUT)
                    .setColor(0f, 0.098f, 1f, 0f, 0.078f, 0.788f, 0.8f)
                    .setAlpha(1.0f, 0.5f)
                    .setScale(0.1F + this.random.nextFloat()*0.3f, 0.01F)
                    .setSpin(this.random.nextBoolean() ? 0.1F : -0.1F)
                    .randomMotion(0.05, 0.05)
                    .randomOffset(0.1, 0.1F)
                    .setGravity(2f)
                    .spawn(this.world, this.getX(), this.getY(), this.getZ());
        }

        if (this.inGround || this.age > 5) {
            for (int i = 0; i < 50; i++) {
                this.getWorld().addParticle(
                        ParticleTypes.ENCHANT,
                        this.getX() + this.random.nextGaussian() * 2.0 * Math.cos((double) this.getYaw()),
                        this.getY(),
                        this.getZ() + this.random.nextGaussian() * 2.0 * Math.cos((double) this.getYaw()),
                        this.random.nextGaussian() / 10.0,
                        (double) (this.random.nextFloat() / 2.0F),
                        this.random.nextGaussian() / 10.0
                );
            }
            this.ticksUntilRemove--;
        }

        if (this.ticksUntilRemove <= 0)
        {
            if(!world.isClient) {
                VortexEntity vortex = new VortexEntity(this.getWorld());
                vortex.setOwnerUuid(owner);
                vortex.setLifetime(maxLifespawn - this.age);
                vortex.setPosition(this.getPos());
                this.world.spawnEntity(vortex);
            }
            this.discard();
        }
    }

    protected SoundEvent getHitSound()
    {
        return AstaSounds.GRIMOIRE_IMPACT;
    }

    public boolean hasNoGravity(){return true;}

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
    }
}
