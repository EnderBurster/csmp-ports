package aureum.asta.disks.mixin.ports.elysium;

import aureum.asta.disks.ports.elysium.armour.ElysiumArmour;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.EquipmentSlot.Type;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({PlayerInventory.class})
public class InventoryMixin {
   @Shadow
   @Final
   public DefaultedList<ItemStack> armor;
   @Shadow
   @Final
   public PlayerEntity player;

   @Inject(
           method = {"damageArmor"},
           at = {@At(
                   value = "INVOKE",
                   shift = Shift.AFTER,
                   target = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"
           )}
   )
   private void elysium$turnExperimentalElysiumIntoIron(
           DamageSource source, float f, int[] is, CallbackInfo ci, @Local(ordinal = 2) int i, @Local ItemStack itemStack
   ) {
      if (itemStack.isIn(ElysiumArmour.EXPERIMENTAL_ELYSIUM_ARMOUR_TAG) && this.player.getRandom().nextInt(4) == 0) {
         EquipmentSlot slot = EquipmentSlot.fromTypeIndex(Type.ARMOR, i);

         this.armor.set(i, new ItemStack(switch (slot) {
            case FEET -> Items.IRON_BOOTS;
            case LEGS -> Items.IRON_LEGGINGS;
            case CHEST -> Items.IRON_CHESTPLATE;
            case HEAD -> Items.IRON_HELMET;
            default -> Items.AIR;
         }));
         this.player.sendEquipmentBreakStatus(slot);
      }
   }
}
