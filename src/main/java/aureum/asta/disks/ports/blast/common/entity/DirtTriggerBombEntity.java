package aureum.asta.disks.ports.blast.common.entity;

import aureum.asta.disks.ports.blast.common.init.BlastItems;
import aureum.asta.disks.ports.blast.common.world.BlockFillingExplosion;
import aureum.asta.disks.ports.blast.common.world.CustomExplosion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class DirtTriggerBombEntity extends TriggerBombEntity {
    public DirtTriggerBombEntity(EntityType<? extends BombEntity> entityType, World world) {
        super(entityType, world);
        this.setExplosionRadius(2f);
    }

    public DirtTriggerBombEntity(EntityType<? extends BombEntity> entityType, World world, LivingEntity livingEntity) {
        super(entityType, world, livingEntity);
        this.setExplosionRadius(2f);
    }

    @Override
    protected Item getDefaultItem() {
        return BlastItems.DIRT_TRIGGER_BOMB;
    }

    @Override
    protected CustomExplosion getExplosion() {
        return new BlockFillingExplosion(this.getWorld(), this.getOwner(), this.getX(), this.getY(), this.getZ(), this.getExplosionRadius(), Blocks.DIRT.getDefaultState());
    }

}
