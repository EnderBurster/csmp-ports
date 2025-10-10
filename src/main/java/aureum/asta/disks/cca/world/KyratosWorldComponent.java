package aureum.asta.disks.cca.world;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.api.lodestone.setup.LodestoneParticles;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.Easing;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.ParticleBuilders;
import aureum.asta.disks.api.lodestone.systems.rendering.particle.SimpleParticleEffect;
import aureum.asta.disks.blocks.BarrierBlock;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.client.render.border.BorderRenderer;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.ports.charter.client.render.DominanceRenderer;
import aureum.asta.disks.ports.charter.common.component.QueuedBlockChange;
import aureum.asta.disks.ports.charter.common.init.CharterParticles;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// I love Arathain
public class KyratosWorldComponent implements AutoSyncedComponent, ServerTickingComponent, ClientTickingComponent {
    public final ArrayList<WaterBarrier> barriers = new ArrayList<>();
    public final World theWorld;
    private final List<QueuedBlockChange> queuedBlockChanges = new ArrayList<>();

    public KyratosWorldComponent(World world) {
        this.theWorld = world;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        NbtList list = tag.getList("barriers", NbtElement.COMPOUND_TYPE);
        this.barriers.clear();
        list.forEach(dNbt -> {
            WaterBarrier barrier = new WaterBarrier();
            barrier.readFromNbt((NbtCompound)dNbt);
            this.barriers.add(barrier);
        });

        NbtList stateList = tag.getList("stateList", 10);
        this.queuedBlockChanges.clear();
        stateList.forEach(dfNbt -> {
            QueuedBlockChange q = new QueuedBlockChange();
            q.readFromNbt((NbtCompound)dfNbt);
            this.queuedBlockChanges.add(q);
        });
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtList list = new NbtList();
        this.barriers.forEach(bar -> {
            NbtCompound dNbt = new NbtCompound();
            bar.writeToNbt(dNbt);
            list.add(dNbt);
        });
        tag.put("barriers", list);

        NbtList blockList = new NbtList();
        this.queuedBlockChanges.forEach(deathfount -> {
            NbtCompound deathfountNbt = new NbtCompound();
            deathfount.writeToNbt(deathfountNbt);
            blockList.add(deathfountNbt);
        });
        tag.put("stateList", blockList);
    }

    public void serverTick() {
        int i = this.barriers.size() + this.queuedBlockChanges.size();
        this.barriers.removeIf(bar -> !(this.theWorld.getBlockState(bar.getQuery()).getBlock() instanceof BarrierBlock) && bar.shouldQuery());
        this.queuedBlockChanges.forEach(s -> s.tick(this.theWorld));
        this.queuedBlockChanges.removeIf(h -> shouldProcQueuedBlock(h, this.theWorld));
        if (i != this.barriers.size() + this.queuedBlockChanges.size()) {
            this.theWorld.syncComponent(AureumAstaDisks.KYRATOS);
        }

        for (WaterBarrier barrier : this.barriers)
        {
            if(barrier.isActive())
            {
                barrier.tick(theWorld);
            }
        }
    }

    public static boolean shouldProcQueuedBlock(QueuedBlockChange change, World world) {
        boolean bl = change.age >= change.maxAge;
        if (change.age == change.maxAge) {
            BlockState st = world.getBlockState(change.pos);
            if (!st.isAir() && world instanceof ServerWorld s) {
                BlockEntity tileentity = st.hasBlockEntity() ? world.getBlockEntity(change.pos) : null;
                Block.getDroppedStacks(st, s, change.pos, tileentity).forEach(stack -> {
                    if (!stack.isEmpty() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS)) {
                        ItemScatterer.spawn(world, change.pos, DefaultedList.ofSize(1, stack));
                    }
                });
                st.onStacksDropped(s, change.pos, ItemStack.EMPTY, false);
            }

            world.setBlockState(change.pos, change.queuedState, 3);
        }

        return bl;
    }

    public void addBlockChange(QueuedBlockChange change) {
        if (!(change.queuedState.getBlock() instanceof BarrierBlock)
                && this.queuedBlockChanges.stream().noneMatch(queuedBlockChange -> queuedBlockChange.pos.equals(change.pos))) {
            this.queuedBlockChanges.add(change);
            this.theWorld.syncComponent(AureumAstaDisks.KYRATOS);
        }
    }

    public WaterBarrier getBarrier(BlockPos pos)
    {
        for (WaterBarrier barrier : this.barriers)
        {
            if(barrier.getCenter().equals(pos))
            {
                return barrier;
            }
        }

        return null;
    }

    public boolean hasPassItem(Entity entity) {
        if(entity instanceof PlayerEntity player)
        {
            for (ItemStack stack : player.getInventory().main) {
                if (stack.isOf(AstaItems.DUK_SWORD)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean insideBarrier(Vec3d pos)
    {
        int insideCount = 0;
        for (WaterBarrier barrier : this.barriers)
        {
            if(barrier.isPosInside(pos))
            {
                insideCount++;
            }
        }

        return insideCount > 0;
    }


    public boolean atBarrier(Vec3d pos)
    {
        int insideCount = 0;
        for (WaterBarrier barrier : this.barriers)
        {
            if(barrier.isAtBorder(pos))
            {
                insideCount++;
            }
        }

        return insideCount > 0;
    }

    public boolean insideBarrier(Vec3d pos, WaterBarrier barrier)
    {
        return barrier.isPosInside(pos);
    }

    public boolean maybeInOutBarrier(Vec3d pos, WaterBarrier barrier)
    {
        return barrier.isReversed() == barrier.isPosInside(pos);
    }

    public boolean atBarrier(Vec3d pos, WaterBarrier barrier)
    {
        return barrier.isAtBorder(pos);
    }

    @Environment(EnvType.CLIENT)
    public void renderTick(WorldRenderContext context) {

        for (WaterBarrier barrier : this.barriers)
        {
            if(!barrier.isActive() || barrier.getAmpBlocks() < 2 || !shouldRenderBarrier(context, barrier))
            {
                continue;
            }

            Vec3d pos = new Vec3d(barrier.getCenter().getX(), barrier.getCenter().getY(), barrier.getCenter().getZ());
            MatrixStack matrices = context.matrixStack();
            Camera camera = context.camera();

            double camX = camera.getPos().x;
            double camY = camera.getPos().y;
            double camZ = camera.getPos().z;

            matrices.push();
            matrices.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);

            float calculatedSize = barrier.getActivating() ? openingAnimation(barrier, context.tickDelta()) : barrier.getSize();

            BorderRenderer.drawBaseCube(matrices, calculatedSize, pos);
            //DominanceRenderer.drawBaseCube(matrices, calculatedSize, pos);
            matrices.pop();
        }
    }

    private boolean shouldRenderBarrier(WorldRenderContext context, WaterBarrier barrier)
    {
        if(!barrier.isActive()) return false;

        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();

        MinecraftClient client = MinecraftClient.getInstance();
        int renderDistanceChunks = client.options.getViewDistance().getValue();
        int renderDistanceBlocks = renderDistanceChunks * 16;

        double maxDistSq = renderDistanceBlocks * renderDistanceBlocks;

        Box box = new Box(barrier.getCenter()).expand(barrier.getSize());

        return squaredDistanceToBox(box, camPos) <= maxDistSq;
    }

    public static double squaredDistanceToBox(Box box, Vec3d point) {
        double dx = Math.max(box.minX - point.x, 0.0);
        dx = Math.max(dx, point.x - box.maxX);

        double dy = Math.max(box.minY - point.y, 0.0);
        dy = Math.max(dy, point.y - box.maxY);

        double dz = Math.max(box.minZ - point.z, 0.0);
        dz = Math.max(dz, point.z - box.maxZ);

        return dx * dx + dy * dy + dz * dz;
    }

    private static float openingAnimation(WaterBarrier barrier, float tickDelta)
    {
        int maxActivatingLifetime = barrier.getMaxActivatingLifetime();
        int activatingLifetime = maxActivatingLifetime - barrier.getActivatingLifetime();

        float size;
        float nextSize;
        float calculatedSize;

        float delay = 30;
        float delaySize = 0.15f;

        if(activatingLifetime < delay)
        {
            calculatedSize = delaySize;
        }
        else
        {
            size = Math.max(barrier.getSize() * ((activatingLifetime-delay) /(maxActivatingLifetime-delay)), delaySize);
            nextSize = Math.max(Math.min(barrier.getSize() * ((activatingLifetime+1-delay) /(maxActivatingLifetime-delay)), barrier.getSize()), delaySize);

            calculatedSize = MathHelper.lerp(tickDelta, size, nextSize);
        }
        return calculatedSize;
    }

    @Environment(EnvType.CLIENT)
    public void clientTick() {
        Random random = this.theWorld.random;
        if (this.theWorld.getTime() % 4L == 0L) {
            ParticleBuilders.WorldParticleBuilder squareBuilder = ParticleBuilders.create(CharterParticles.SQUARE)
                    .setScale(0.025F + random.nextFloat() * 0.05F, 0.0F)
                    .setScaleEasing(Easing.SINE_IN)
                    .setAlpha(0.0F, 0.9F + random.nextFloat() * 0.1F, 0.0F)
                    .setAlphaEasing(Easing.SINE_OUT, Easing.SINE_IN)
                    .setAlphaCoefficient(1.2F)
                    .setColorCoefficient(0.65F)
                    .setColorEasing(Easing.SINE_IN)
                    .setSpinEasing(Easing.SINE_IN, Easing.CUBIC_OUT)
                    .setSpinCoefficient(1.5F)
                    .enableNoClip();
            ParticleBuilders.WorldParticleBuilder smokeBuilder = ParticleBuilders.create(LodestoneParticles.SMOKE_PARTICLE)
                    .setAlpha(0.0F, 0.06F, 0.04F)
                    .setAlphaEasing(Easing.SINE_IN)
                    .setScale(0.0F, 0.2F, 0.1F)
                    .setScaleEasing(Easing.SINE_IN)
                    .setColorEasing(Easing.SINE_IN)
                    .randomOffset(0.1F, 0.2F)
                    .overrideRemovalProtocol(SimpleParticleEffect.SpecialRemovalProtocol.ENDING_CURVE_INVISIBLE)
                    .enableNoClip();

            for (QueuedBlockChange c : this.queuedBlockChanges) {
                int lifetime = (int)(40.0 / ((double)random.nextFloat() * 0.8 + 0.2));
                int spinDirection = random.nextBoolean() ? 1 : -1;
                int spinOffset = random.nextInt(360);
                float spinStrength = 0.5F + random.nextFloat() * 0.25F;
                float colorTilt = (0.7F + random.nextFloat() * 0.3F) / 255.0F;
                Color startingColor = new Color(77.0F * colorTilt, 83.0F * colorTilt, 250.0F * colorTilt);
                Color endingColor = new Color(0.0F * colorTilt, 222.0F * colorTilt, 255.0F * colorTilt);
                squareBuilder.setLifetime(lifetime)
                        .setSpin(0.0F, spinStrength * (float)spinDirection, 0.0F)
                        .setSpinOffset((float)spinOffset)
                        .setColor(startingColor, endingColor)
                        .evenlySpawnAtAlignedEdges(this.theWorld, c.pos, c.queuedState, 9);
                smokeBuilder.setLifetime(lifetime)
                        .setSpin(0.0F, spinStrength * (float)spinDirection * 0.1F, 0.0F)
                        .setSpinOffset((float)spinOffset)
                        .setColor(startingColor, endingColor)
                        .evenlySpawnAtAlignedEdges(this.theWorld, c.pos, c.queuedState, 15);
            }
        }

        for (WaterBarrier barrier : this.barriers)
        {
            if(barrier.isActive())
            {
                barrier.clientTick(theWorld);
            }
        }
    }
}
