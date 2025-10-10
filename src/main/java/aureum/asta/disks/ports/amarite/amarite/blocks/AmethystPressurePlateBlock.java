package aureum.asta.disks.ports.amarite.amarite.blocks;

import net.minecraft.block.BlockSetType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.World;
import net.minecraft.block.AbstractPressurePlateBlock;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Property;
import net.minecraft.sound.SoundEvents;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager.Builder;
import org.jetbrains.annotations.NotNull;

public class AmethystPressurePlateBlock extends AbstractPressurePlateBlock {
   public static final BooleanProperty POWERED = Properties.POWERED;

   public AmethystPressurePlateBlock(Settings settings, BlockSetType blockSetType) {
      super(settings, blockSetType);
      this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWERED, Boolean.FALSE));
   }

   protected int getRedstoneOutput(@NotNull BlockState state) {
      return state.get(POWERED) ? 15 : 0;
   }

   protected BlockState setRedstoneOutput(@NotNull BlockState state, int rsOut) {
      return (BlockState)state.with(POWERED, rsOut > 0);
   }

   protected void method_9436(@NotNull WorldAccess world, BlockPos pos) {
      world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundCategory.BLOCKS, 0.9F, 1.6F);
      world.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.7F);
   }

   protected void method_9438(@NotNull WorldAccess world, BlockPos pos) {
      world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundCategory.BLOCKS, 0.9F, 1.1F);
      world.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.6F);
   }

   protected int getRedstoneOutput(@NotNull World world, BlockPos pos) {
      for (PlayerEntity entity : world.getNonSpectatingEntities(PlayerEntity.class, BOX.offset(pos))) {
         if (!entity.canAvoidTraps()) {
            return 15;
         }
      }

      return 0;
   }

   protected void appendProperties(@NotNull Builder<Block, BlockState> builder) {
      builder.add(new Property[]{POWERED});
   }
}
