package aureum.asta.disks.ports.blast.common.entity;

import aureum.asta.disks.ports.blast.common.init.BlastItems;
import aureum.asta.disks.ports.blast.common.world.CustomExplosion;
import aureum.asta.disks.ports.blast.common.world.KnockbackExplosion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class SlimeBombEntity extends BombEntity {
    public SlimeBombEntity(EntityType<? extends BombEntity> entityType, World world) {
        super(entityType, world);
    }

    public SlimeBombEntity(EntityType<? extends BombEntity> entityType, World world, LivingEntity livingEntity) {
        super(entityType, world, livingEntity);
    }

    @Override
    protected Item getDefaultItem() {
        return BlastItems.SLIME_BOMB;
    }

    @Override
    protected CustomExplosion getExplosion() {
        return new KnockbackExplosion(this.getWorld(), this.getOwner(), this.getX(), this.getY(), this.getZ(), this.getExplosionRadius());
    }
}
