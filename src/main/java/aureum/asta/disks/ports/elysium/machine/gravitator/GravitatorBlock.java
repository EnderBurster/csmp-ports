package aureum.asta.disks.ports.elysium.machine.gravitator;

import aureum.asta.disks.ports.elysium.machine.ElysiumMachineBlock;
import aureum.asta.disks.ports.elysium.machine.ElysiumMachines;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class GravitatorBlock extends ElysiumMachineBlock {
   public final boolean isOutwards;

   public GravitatorBlock(Settings properties, boolean isOutwards) {
      super(properties);
      this.isOutwards = isOutwards;
   }

   @Nullable
   public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
      return ElysiumMachines.GRAVITATOR_BE.instantiate(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
      return checkType(type, ElysiumMachines.GRAVITATOR_BE, (l, p, s, e) -> GravitatorBlockEntity.tick(l, p, s, e, this.isOutwards));
   }
}
