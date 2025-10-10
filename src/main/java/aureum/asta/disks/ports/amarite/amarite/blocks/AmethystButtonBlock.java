package aureum.asta.disks.ports.amarite.amarite.blocks;

import net.minecraft.block.BlockSetType;
import net.minecraft.block.ButtonBlock;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class AmethystButtonBlock extends ButtonBlock {
   public AmethystButtonBlock(Settings settings, BlockSetType blockSetType, int pressTicks, boolean wooden) {
      super(settings, blockSetType, pressTicks, wooden);
   }

   protected SoundEvent getClickSound(boolean powered) {
      return SoundEvents.BLOCK_AMETHYST_BLOCK_STEP;
   }
}
