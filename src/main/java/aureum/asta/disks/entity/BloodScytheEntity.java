package aureum.asta.disks.entity;

import aureum.asta.disks.index.ArsenalDamageTypes;
import aureum.asta.disks.index.ArsenalEntities;
import aureum.asta.disks.index.ArsenalParticles;
import aureum.asta.disks.index.ArsenalSounds;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BloodScytheEntity extends PersistentProjectileEntity {
    private final Set<StatusEffectInstance> effects = Sets.newHashSet();
    public int ticksUntilRemove = 5;
    public final List<LivingEntity> hitEntities = new ArrayList<>();

    public BloodScytheEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world)
    {
        super(entityType, world);
    }

    public BloodScytheEntity(World world, LivingEntity owner) { super(ArsenalEntities.BLOOD_SCYTHE, owner, world);}

    protected ItemStack asItemStack()
    {
        return ItemStack.EMPTY;
    }

    public void addEffect(StatusEffectInstance effect) { this.effects.add(effect);}

    public void tick() {
        super.tick();

        for (float x = -3.0F; x <= 3.0F; x = (float) ((double) x + 0.1)) {
            this.getWorld()
                    .addParticle(
                            ArsenalParticles.BLOOD_BUBBLE,
                            this.getX() + (double) x * Math.cos((double) this.getYaw()),
                            this.getY(),
                            this.getZ() + (double) x * Math.sin((double) this.getYaw()),
                            this.getVelocity().getX(),
                            this.getVelocity().getY(),
                            this.getVelocity().getZ()
            );
        }

        if (this.inGround || this.age > 20) {
            for (int i = 0; i < 50; i++) {
                this.getWorld().addParticle(
                        ArsenalParticles.BLOOD_BUBBLE_SPLATTER,
                        this.getX() + this.random.nextGaussian() * 2.0 * Math.cos((double) this.getYaw()),
                        this.getY(),
                        this.getZ() + this.random.nextGaussian() * 2.0 * Math.sin((double) this.getYaw()),
                        this.random.nextGaussian() / 10.0,
                        (double) (this.random.nextFloat() / 2.0F),
                        this.random.nextGaussian() / 10.0
                );
            }
            this.ticksUntilRemove--;
        }

        if (this.ticksUntilRemove <= 0)
        {
            this.discard();
        }

        if (!this.getWorld().isClient) {
            for (LivingEntity livingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox(), livingEntity -> this.getOwner() != livingEntity)) {
                livingEntity.damage(this.getWorld().getDamageSources().create(ArsenalDamageTypes.BLOOD_SCYTHE, this, this.getOwner()), 12.0F);

                for (StatusEffectInstance effect : this.effects) {
                    livingEntity.addStatusEffect(effect);
                }
            }
        }
    }

    protected SoundEvent getHitSound()
    {
        return ArsenalSounds.ENTITY_BLOOD_SCYTHE_HIT;
    }

    public boolean hasNoGravity(){return true;}

    protected void onEntityHit(EntityHitResult entityHitResult){}
}
