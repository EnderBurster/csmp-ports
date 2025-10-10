package aureum.asta.disks.ports.blast.common.entity;

import aureum.asta.disks.ports.blast.common.init.BlastEntities;
import aureum.asta.disks.ports.blast.common.init.BlastItems;
import aureum.asta.disks.ports.blast.common.world.CustomExplosion;
import aureum.asta.disks.ports.blast.common.world.EntityExplosion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class AmethystTriggerBombEntity extends TriggerBombEntity {
    public AmethystTriggerBombEntity(EntityType<? extends BombEntity> entityType, World world) {
        super(entityType, world);
        this.setExplosionRadius(70f);
    }

    public AmethystTriggerBombEntity(EntityType<? extends BombEntity> entityType, World world, LivingEntity livingEntity) {
        super(entityType, world, livingEntity);
        this.setExplosionRadius(70f);
    }

    @Override
    protected Item getDefaultItem() {
        return BlastItems.AMETHYST_TRIGGER_BOMB;
    }

    @Override
    protected CustomExplosion getExplosion() {
        return new EntityExplosion(this.getWorld(), this.getOwner(), this.getX(), this.getY(), this.getZ(), BlastEntities.AMETHYST_SHARD, Math.round(this.getExplosionRadius()), 1.4f);
    }

}
