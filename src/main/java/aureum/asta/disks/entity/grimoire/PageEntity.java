package aureum.asta.disks.entity.grimoire;

import aureum.asta.disks.effect.AstaStatusEffects;
import aureum.asta.disks.init.AstaEntities;
import aureum.asta.disks.ports.mason.entity.TameableHostileEntity;
import aureum.asta.disks.sound.AstaSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class PageEntity extends PersistentProjectileEntity {
    public int ticksUntilRemove = 5;

    public PageEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public PageEntity(World world, LivingEntity owner) {
        super(AstaEntities.GRIMOIRE_PAGE, owner, world);
    }

    public PageEntity(World world, Vec3d pos) {
        super(AstaEntities.GRIMOIRE_PAGE, pos.getX(), pos.getY(), pos.getZ(), world);
    }

    @Override
    protected ItemStack asItemStack() {
        return ItemStack.EMPTY;
    }

    public void tick()
    {
        super.tick();

        Vec3d vel = this.getVelocity();
        int steps = 6;

        for (int step = 0; step < steps; step++) {
            double px = this.getX() - vel.x * step / steps;
            double py = this.getY() - vel.y * step / steps;
            double pz = this.getZ() - vel.z * step / steps;

            for (float x = -0.5F; x <= 0.5F; x += 0.05F) {
                this.getWorld().addParticle(
                        ParticleTypes.ENCHANT,
                        px - x * Math.cos(this.getYaw()),
                        py,
                        pz - x * Math.sin(this.getYaw()),
                        vel.x,
                        vel.y,
                        vel.z
                );
            }
        }

        if (this.inGround || this.age > 20) {
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
            this.discard();
        }

        if (!this.getWorld().isClient) {
            for (LivingEntity livingEntity : this.getWorld().getEntitiesByClass(LivingEntity.class, this.getBoundingBox(), livingEntity -> !livingEntity.equals(this.getOwner()) && !(livingEntity instanceof TameableEntity tamed && tamed.getOwner() != null && tamed.getOwner().equals(this.getOwner())) && !(livingEntity instanceof ArmorStandEntity) && !(livingEntity instanceof TameableHostileEntity tameableHostile && tameableHostile.isOwner((LivingEntity) this.getOwner())) && !(livingEntity instanceof PlayerEntity player && player.getUuid().equals(UUID.fromString("e0f927bf-91fe-3015-97d8-41698d0cd92d"))))) {
                livingEntity.damage(this.getWorld().getDamageSources().create(DamageTypes.GENERIC, this, this.getOwner()), (float) this.getDamage());

                Random r= new Random();
                int r1 = r.nextInt(Math.max(livingEntity.getArmor(), 1));

                if (r1 < 10)
                {
                    livingEntity.addStatusEffect(new StatusEffectInstance(AstaStatusEffects.BLEED, 20*3, 1));
                }
            }
        }
    }

    protected SoundEvent getHitSound()
    {
        return AstaSounds.GRIMOIRE_IMPACT;
    }

    public boolean hasNoGravity(){return true;}

    protected void onEntityHit(EntityHitResult hitResult)
    {
    }
}
