package aureum.asta.disks.blocks;

import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.init.AstaBlockEntities;
import aureum.asta.disks.init.AstaBlocks;
import aureum.asta.disks.recipe.RuneCraftingRecipe;
import aureum.asta.disks.sound.AstaSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;


public class CreationBlockEntity extends BlockEntity {

    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private boolean crafting = false;
    private final int MAX_CRAFTING_TIME = 40;
    private int craftingTime = 0;
    private boolean locked = false;

    public CreationBlockEntity(BlockPos pos, BlockState state) {
        super(AstaBlockEntities.CREATION_BLOCK, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        Inventories.writeNbt(nbt, items, true);

        nbt.putBoolean("Crafting", this.crafting);
        nbt.putBoolean("Locked", locked);

        nbt.putInt("CraftingTime", this.craftingTime);

        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        this.crafting = nbt.getBoolean("Crafting");
        this.locked = nbt.getBoolean("Locked");

        this.craftingTime = nbt.getInt("CraftingTime");

        this.items.clear();
        Inventories.readNbt(nbt, items);
    }

    public boolean isCrafting()
    {
        return this.crafting;
    }

    public boolean isLocked()
    {
        return this.locked;
    }

    public void setLocked(boolean newLocked)
    {
        this.locked = newLocked;
    }

    public static void tick(World world, BlockPos pos, BlockState state, CreationBlockEntity blockEntity) {
        if(blockEntity.craftingTime > 1)
        {
            blockEntity.craftingTime--;
            blockEntity.crafting = true;
            //world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_CLUSTER_STEP, SoundCategory.BLOCKS, 1f, 1f);
        }
        else blockEntity.crafting = false;

        if(blockEntity.craftingTime == 5)
        {
            world.playSound(null, pos, AstaSounds.GRIMOIRE_WHISPERS, SoundCategory.BLOCKS);
        }
        else if(blockEntity.craftingTime == 1)
        {
            blockEntity.craftingTime--;
            blockEntity.items.set(0, blockEntity.items.get(1));
            blockEntity.setLocked(false);
            clearPedestalItems(world, pos, 3);
            blockEntity.spawnExplosion(world, pos.toCenterPos());
            world.playSound(null, pos, SoundEvents.BLOCK_AMETHYST_BLOCK_BREAK, SoundCategory.BLOCKS, 2f, 1f);
        }
    }

    @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public boolean addItem(ItemStack stack) {
        if (items.get(0).isEmpty()) {
            items.set(0, stack);
            return true;
        }
        return false;
    }

    public ItemStack removeLastItem() {
        if (!items.get(0).isEmpty()) {
            ItemStack stack = items.get(0);
            items.set(0, ItemStack.EMPTY);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void sync() {
        if (this.world != null) {
            this.markDirty();
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
        }
    }

    public void tryCraft(ServerWorld world) {
        ItemStack runeItem = this.items.get(0);
        List<ItemStack> pedestalItems = getPedestalItems(world, this.pos, 3);

        Optional<RuneCraftingRecipe> match = world.getRecipeManager()
                .listAllOfType(RuneCraftingRecipe.Type.INSTANCE)
                .stream()
                .filter(r -> r.matches(runeItem, pedestalItems))
                .findFirst();

        if (match.isPresent()) {
            this.craft(match, world);
        }
    }

    public void craft(Optional<RuneCraftingRecipe> match, ServerWorld world)
    {
        RuneCraftingRecipe recipe = match.get();

        this.items.set(1, recipe.getOutput(world.getRegistryManager()));
        this.locked = true;
        this.craftingTime = MAX_CRAFTING_TIME;
        for (AmpBlockEntity block : getPedestals(world, this.pos, 3)) {
            block.setLocked(true);
        }

        world.playSound(null, pos, AstaSounds.GRIMOIRE_WHISPERS, SoundCategory.BLOCKS);

        this.sync();
    }

    public static void clearPedestalItems(World world, BlockPos center, int distance) {

        DefaultedList<AmpBlockEntity> blocks = getPedestals(world, center, distance);

        for (AmpBlockEntity block : blocks) {
            block.setLocked(false);
            block.removeLastItem();
            block.sync();
        }
    }

    public static DefaultedList<ItemStack> getPedestalItems(World world, BlockPos center, int distance) {

        DefaultedList<ItemStack> items = DefaultedList.ofSize(4, ItemStack.EMPTY);
        DefaultedList<AmpBlockEntity> blocks = getPedestals(world, center, distance);

        for (int i = 0; i < blocks.size(); i++) {
            if (blocks.get(i).getItems() != null) {
                items.set(i, blocks.get(i).getItems().get(0));
            }
        }

        return items;
    }

    public static DefaultedList<AmpBlockEntity> getPedestals(World world, BlockPos center, int distance) {

        DefaultedList<AmpBlockEntity> blocks = DefaultedList.ofSize(4);

        BlockPos[] offsets = new BlockPos[]{
                center.north(distance),
                center.south(distance),
                center.east(distance),
                center.west(distance)
        };

        for (int i = 0; i < offsets.length; i++) {
            if (world.getBlockState(offsets[i]).isOf(AstaBlocks.AMP_RUNE) && ((AmpBlockEntity)world.getBlockEntity(offsets[i])) != null) {
                blocks.add(i, ((AmpBlockEntity)world.getBlockEntity(offsets[i])));
            }
        }

        return blocks;
    }

    public void spawnExplosion(World world, Vec3d origin)
    {
        for (int i = 0; i < 60; i++)
        {
            ParticleBuilders.create(LodestoneParticles.WISP_PARTICLE)
                    .setLifetime(15)
                    .overrideAnimator(SimpleParticleEffect.Animator.WITH_AGE)
                    .setAlphaEasing(Easing.BOUNCE_IN_OUT)
                    .setColorCoefficient(2.0f)
                    .setColorEasing(Easing.ELASTIC_OUT)
                    .setSpinEasing(Easing.SINE_IN)
                    .setScaleEasing(Easing.SINE_IN_OUT)
                    .setColor(0f, 0.098f, 1f, 0f, 0.078f, 0.788f, 0.8f)
                    .setAlpha(1.0f, 0.5f)
                    .setScale(0.2F + world.random.nextFloat()*0.05f, 0.015f)
                    .setSpin(world.random.nextBoolean() ? 0.1F : -0.1F)
                    .randomMotion(1, 1)
                    .randomOffset(0.1f, 0.1f)
                    .spawn(world, origin.getX(), origin.getY() + 1f, origin.getZ());
        }
    }

}
