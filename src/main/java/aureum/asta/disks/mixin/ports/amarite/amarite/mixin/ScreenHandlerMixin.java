package aureum.asta.disks.mixin.ports.amarite.amarite.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import aureum.asta.disks.ports.amarite.amarite.items.MaskItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteSoundEvents;

@Mixin({ScreenHandler.class})
public class ScreenHandlerMixin {
   @Shadow
   @Final
   public DefaultedList<Slot> slots;

   @Inject(
      method = {"internalOnSlotClick"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void amarite$preventStacking(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
      if (actionType == SlotActionType.PICKUP && button == 1 && slotIndex >= 0 && slotIndex < this.slots.size()) {
         Slot slot = (Slot)this.slots.get(slotIndex);
         ItemStack stack = slot.getStack();
         if (stack.getItem() instanceof MaskItem) {
            MaskItem.incrementOffset(stack);
            player.playSound(AmariteSoundEvents.MASK_OFFSET, SoundCategory.PLAYERS, 0.9F, 1.5F);
            ci.cancel();
         }
      }
   }
}
