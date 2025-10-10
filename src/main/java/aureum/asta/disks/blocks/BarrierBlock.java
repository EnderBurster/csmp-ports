package aureum.asta.disks.blocks;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.init.AstaBlockEntities;
import aureum.asta.disks.init.AstaBlocks;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.item.custom.GrimoireItem;
import aureum.asta.disks.sound.AstaSounds;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BarrierBlock extends BlockWithEntity {
    public static final EnumProperty<Direction.Axis> AXIS;

    public BarrierBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)this.getDefaultState().with(AXIS, Direction.Axis.Y));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BarrierBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return checkType(type, AstaBlockEntities.BARRIER_BLOCK, BarrierBlockEntity::tick);
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (placer instanceof PlayerEntity) {
            WaterBarrier bar = new WaterBarrier(75, pos);
            bar.setOwner(placer.getUuid());
            (world.getComponent(AureumAstaDisks.KYRATOS)).barriers.add(bar);
            world.syncComponent(AureumAstaDisks.KYRATOS);
        }

        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        WaterBarrier barrier = world.getComponent(AureumAstaDisks.KYRATOS).getBarrier(pos);
        if(barrier != null && barrier.isActive())
        {
            dropStack(world, pos, AstaItems.GRIMOIRE.getDefaultStack());
        }
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
        WaterBarrier barrier = world.getComponent(AureumAstaDisks.KYRATOS).getBarrier(pos);
        if(barrier == null)
        {
            return ActionResult.FAIL;
        }

        DefaultedList<Boolean> ampBlocks = hasBlocksInCardinals(world, pos, 3);
        if (!barrier.isActive() && player.getMainHandStack().getItem() instanceof GrimoireItem && !barrier.getActivating() && world.getComponent(AureumAstaDisks.KYRATOS).hasPassItem(player) && barrier.getAmpBlocks(ampBlocks) != 0) {
            barrier.setActive(true, ampBlocks);
            barrier.setSize(75);

            if(barrier.getAmpBlocks(ampBlocks) == 1) {
                world.playSound(null, pos, AstaSounds.BARRIER_VOCALS_1, SoundCategory.PLAYERS, 4.0f, 1.0f);
                barrier.spawnExplosion(world, pos.toCenterPos());
            }
            else if(barrier.getAmpBlocks(ampBlocks) == 2) world.playSound(null, pos, AstaSounds.BARRIER_VOCALS_2, SoundCategory.PLAYERS, 4.0f, 1.0f);
            else if(barrier.getAmpBlocks(ampBlocks) == 3) world.playSound(null, pos, AstaSounds.BARRIER_VOCALS_3, SoundCategory.PLAYERS, 4.0f, 1.0f);
            else if(barrier.getAmpBlocks(ampBlocks) == 4) world.playSound(null, pos, AstaSounds.BARRIER_VOCALS, SoundCategory.PLAYERS, 4.0f, 1.0f);

            if(barrier.getAmpBlocks(ampBlocks) >= 2)
            {
                if(world instanceof ServerWorld server)
                {
                    spawnFountain(server, pos, 30);
                }
                barrier.setActivating(true);
            }

            player.getMainHandStack().decrement(1);
            return ActionResult.SUCCESS;
        } else if (barrier.isActive() && world.getComponent(AureumAstaDisks.KYRATOS).hasPassItem(player) && !barrier.getActivating()) {
            player.giveItemStack(AstaItems.GRIMOIRE.getDefaultStack());
            barrier.setActive(false, DefaultedList.ofSize(4, false));
            lockAmpBlocks(world, pos, 3, false);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static void spawnFountain(ServerWorld world, BlockPos origin, int height) {
        double x = origin.getX() + 0.5; // center on block
        double z = origin.getZ() + 0.5;

        for (int y = 0; y < height; y++) {
            double yPos = origin.getY() + y;
            for (int i = 0; i < 5; i++) {
                world.spawnParticles(ParticleTypes.ENCHANT, x, yPos, z, 5, 0.1, 0.1, 0.1, 0);
            }
        }

        world.spawnParticles(ParticleTypes.ENCHANT, x, origin.getY() + 1.5, z, 30, 0.3, 0.01, 0.3, 0.1);
    }

    /**
     * Checks if the given block is placed 2 blocks away in each cardinal direction.
     *
     * @param world the world to check in
     * @param center the center BlockPos
     * @param distance the distance from the center to the block to check for
     * @return list of the target blocks that is placed in the cardinal positions
     */
    public static DefaultedList<Boolean> hasBlocksInCardinals(World world, BlockPos center, int distance) {
        DefaultedList<Boolean> list = DefaultedList.ofSize(4, false);
        BlockPos[] offsets = new BlockPos[]{
                center.north(distance),
                center.east(distance),
                center.south(distance),
                center.west(distance)
        };

        for (int i = 0; i < offsets.length; i++)
        {
            if (world.getBlockState(offsets[i]).isOf(AstaBlocks.AMP_RUNE) && ((AmpBlockEntity)world.getBlockEntity(offsets[i])).getItems() != null && ((AmpBlockEntity)world.getBlockEntity(offsets[i])).getItems().get(0).isItemEqual(Items.HEART_OF_THE_SEA.getDefaultStack())) {
                list.set(i, true);
            }
        }

        if(list.contains(true)) lockAmpBlocks(world, center, distance, true);

        return list;
    }

    public static void lockAmpBlocks(World world, BlockPos center, int distance, boolean locked)
    {
        BlockPos[] offsets = new BlockPos[]{
                center.north(distance),
                center.east(distance),
                center.south(distance),
                center.west(distance)
        };

        for (BlockPos offset : offsets) {
            if (world.getBlockState(offset).isOf(AstaBlocks.AMP_RUNE)) {
                ((AmpBlockEntity) world.getBlockEntity(offset)).setLocked(locked);
            }
        }
    }
}