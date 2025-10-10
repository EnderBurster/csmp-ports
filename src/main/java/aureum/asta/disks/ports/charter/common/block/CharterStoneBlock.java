package aureum.asta.disks.ports.charter.common.block;

import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.component.CharterWorldComponent;
import aureum.asta.disks.ports.charter.common.component.DiamondOfProtection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CharterStoneBlock extends Block {
   public CharterStoneBlock(Settings settings) {
      super(settings);
   }

   public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
      if (placer instanceof PlayerEntity) {
         DiamondOfProtection dia = new DiamondOfProtection(32.0, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
         dia.setOwner(placer.getUuid());
         ((CharterWorldComponent)world.getComponent(CharterComponents.CHARTER)).diamonds.add(dia);
         world.syncComponent(CharterComponents.CHARTER);
      }

      super.onPlaced(world, pos, state, placer, itemStack);
   }

   public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
      return createCuboidShape(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
   }
}
