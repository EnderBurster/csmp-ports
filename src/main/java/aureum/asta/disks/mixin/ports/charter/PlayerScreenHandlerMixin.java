package aureum.asta.disks.mixin.ports.charter;

import aureum.asta.disks.ports.charter.common.component.CharterArmComponent;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin({PlayerScreenHandler.class})
public abstract class PlayerScreenHandlerMixin extends AbstractRecipeScreenHandler<CraftingInventory> {
   public PlayerScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
      super(screenHandlerType, i);
   }

   @ModifyArg(
      method = {"<init>"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/screen/PlayerScreenHandler;addSlot(Lnet/minecraft/screen/slot/Slot;)Lnet/minecraft/screen/slot/Slot;",
         ordinal = 5
      ),
      index = 0
   )
   private Slot charter$cursed(Slot in) {
      return new Slot(in.inventory, 40, 77, 62) {
         public Pair<Identifier, Identifier> getBackgroundSprite() {
            return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT);
         }

         public boolean canInsert(ItemStack stack) {
            return ((CharterArmComponent)((PlayerInventory)this.inventory).player.getComponent(CharterComponents.ARM_COMPONENT)).hasArm();
         }
      };
   }
}
