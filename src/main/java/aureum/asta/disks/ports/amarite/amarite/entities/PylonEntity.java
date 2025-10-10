package aureum.asta.disks.ports.amarite.amarite.entities;

import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.DiscPylonComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class PylonEntity extends PersistentProjectileEntity {
   public PylonEntity(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
      super(entityType, world);
   }

   public void tick() {
      super.tick();
      if (((DiscPylonComponent) Amarite.PYLON.get(this)).pylonCharge <= 0) {
         this.discard();
      }
   }

   protected void onEntityHit(@NotNull EntityHitResult entityHitResult) {
      DiscPylonComponent var10000 = (DiscPylonComponent)Amarite.PYLON.get(entityHitResult.getEntity());
      var10000.pylonCharge = var10000.pylonCharge + ((DiscPylonComponent)Amarite.PYLON.get(this)).pylonCharge;
      this.discard();
   }

   protected ItemStack asItemStack() {
      return ItemStack.EMPTY;
   }
}
