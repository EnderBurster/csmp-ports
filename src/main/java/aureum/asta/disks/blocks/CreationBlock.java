package aureum.asta.disks.blocks;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.init.AstaBlockEntities;
import aureum.asta.disks.init.AstaBlocks;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.item.custom.GrimoireItem;
import aureum.asta.disks.recipe.RuneCraftingRecipe;
import aureum.asta.disks.sound.AstaSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CreationBlock extends BlockWithEntity {
    public static final EnumProperty<Direction.Axis> AXIS;

    public CreationBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CreationBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, AstaBlockEntities.CREATION_BLOCK, CreationBlockEntity::tick);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (!world.isClient) {
            CreationBlockEntity be = (CreationBlockEntity) world.getBlockEntity(pos);
            ItemStack stack = be.getItems().get(0);

            if(stack.isEmpty()) return;
            for(AmpBlockEntity block : CreationBlockEntity.getPedestals(world, pos, 3) )
            {
                block.setLocked(false);
            }
            dropStack(world, pos, stack);
        }

        super.onBreak(world, pos, state, player);
    }

    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return changeRotation(state, rotation);
    }

    public static BlockState changeRotation(BlockState state, BlockRotation rotation) {
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                switch ((Direction.Axis)state.get(AXIS)) {
                    case X -> {
                        return (BlockState)state.with(AXIS, Direction.Axis.Z);
                    }
                    case Z -> {
                        return (BlockState)state.with(AXIS, Direction.Axis.X);
                    }
                    default -> {
                        return state;
                    }
                }
            default:
                return state;
        }
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{AXIS});
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState)this.getDefaultState().with(AXIS, ctx.getSide().getAxis());
    }

    static {
        AXIS = Properties.AXIS;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!world.isClient && hasBlocksInCardinals(world, pos, AstaBlocks.AMP_RUNE, 3)) {
            CreationBlockEntity be = (CreationBlockEntity) world.getBlockEntity(pos);
            ItemStack held = player.getStackInHand(hand);

            if(be.isLocked()) return ActionResult.PASS;

            if (!be.getItems().get(0).isEmpty()) {
                ItemStack removed = be.removeLastItem();
                be.sync();
                if (!removed.isEmpty()) {
                    player.getInventory().offerOrDrop(removed);
                }
            } else {
                ItemStack copy = held.copy();
                copy.setCount(1);
                if (be.addItem(copy)) {
                    held.decrement(1);
                    be.sync();
                    be.tryCraft((ServerWorld) world);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    // Merge with other check later into util file.
    public static boolean hasBlocksInCardinals(World world, BlockPos center, Block targetBlock, int distance) {
        // Offsets for N, S, E, W (2 blocks away)
        BlockPos[] offsets = new BlockPos[]{
                center.north(distance),
                center.south(distance),
                center.east(distance),
                center.west(distance)
        };

        for (BlockPos pos : offsets) {
            if (!world.getBlockState(pos).isOf(targetBlock)) {
                return false;
            }
        }

        return true;
    }
}