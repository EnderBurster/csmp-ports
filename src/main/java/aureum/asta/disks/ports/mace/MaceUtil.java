package aureum.asta.disks.ports.mace;


import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class MaceUtil {
   public static void itemStackDamage(ItemStack itemStack, int amount, LivingEntity entity, EquipmentSlot slot) {
      World var5 = entity.getWorld();
      if (var5 instanceof ServerWorld serverWorld) {
         if (!(entity instanceof ServerPlayerEntity)) {
            ServerPlayerEntity var10003 = null;
            return;
         }

         ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity)entity;
         itemStack.damage(amount, serverPlayerEntity, (item) -> entity.sendEquipmentBreakStatus(slot));
      }

   }
}

