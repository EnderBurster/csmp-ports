package aureum.asta.disks.entity;

import moriyashiine.enchancement.common.component.entity.LeechComponent;
import moriyashiine.enchancement.common.init.ModEnchantments;
import moriyashiine.enchancement.common.init.ModEntityComponents;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.world.World;

public class HellforkEntity extends ImpaledTridentEntity {
    public HellforkEntity(EntityType<? extends HellforkEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void onHit(LivingEntity target) {
        super.onHit(target);
        target.setOnFireFor(8);

        if (!this.getWorld().isClient) {
            for (TridentEntity entity : this.getWorld().getEntitiesByClass(TridentEntity.class, target.getBoundingBox().expand(1), entity -> this.getOwner() != target)) {
                LeechComponent leechComponent = (LeechComponent)ModEntityComponents.LEECH.get(entity);
            }
        }
    }

    @Override
    public boolean isOnFire() {
        return true;
    }

    @Override
    public boolean doesRenderOnFire() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isSubmergedInWater() && this.world.isClient() && this.random.nextInt(5) == 0) {
            this.world.addParticle(ParticleTypes.BUBBLE_COLUMN_UP, this.getX() + random.nextGaussian() / 10, this.getY() + random.nextGaussian() / 10, this.getZ() + random.nextGaussian() / 10, 0, this.random.nextFloat(), 0);
        }
    }
}
