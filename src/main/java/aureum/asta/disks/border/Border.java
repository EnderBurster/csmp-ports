package aureum.asta.disks.border;

import aureum.asta.disks.AureumAstaDisks;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;

//Obsolete, will delete later, keeping for reference.
public class Border {

    public static VoxelShape voxelShape = null;
    public static Box borderBox = null;
    public static float borderSize = 10;
    public static int maxBorderSize = 136;

    private static boolean changingSize = false;
    private static int shrinkTicksRemaining = 0;
    private static float startSize = maxBorderSize;
    private static float targetSize = maxBorderSize;
    private static int totalTicks = 0;

    private static final ArrayList<BlockPos> activeBlocks = new ArrayList<>();
    private static BlockPos activeBlock = null;

    public static void addActiveBlock(BlockPos pos) {
        activeBlocks.add(pos);
        AureumAstaDisks.LOGGER.info(String.valueOf(pos));
        AureumAstaDisks.LOGGER.info(String.valueOf(activeBlocks));
    }

    public static void removeActiveBlock(BlockPos pos) {
        activeBlocks.remove(pos);
        activeBlocks.trimToSize();
    }

    public static ArrayList<BlockPos> getActiveBlocks() {
        return activeBlocks;
    }

    public static void setActiveBlock(BlockPos pos) {
        activeBlock = pos;
    }

    public static BlockPos getActiveBlock() {
        return activeBlock;
    }

    // Creates a cube-shaped VoxelShape at a given center with given size
    public static VoxelShape createCollisionCube(double centerX, double centerY, double centerZ, double size) {
        double half = size / 2.0;

        Box box = new Box(
                centerX - half, centerY - half, centerZ - half,
                centerX + half, centerY + half, centerZ + half
        );

        return VoxelShapes.cuboid(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public static void adjustBorderSize(float start, float target, int durationTicks)
    {
        startSize = start;
        targetSize = target;
        shrinkTicksRemaining = durationTicks;
        totalTicks = durationTicks;
    }

    public static void updateBorder() {
        if (shrinkTicksRemaining > 0) {
            // How much to shrink per step
            double shrinkPerStep = 0.1; // adjust if you want finer/coarser shrink increments
            double totalShrink = startSize - targetSize;
            int totalSteps = (int)Math.ceil(totalShrink / shrinkPerStep);

            // How many ticks between each step
            int ticksPerStep = totalTicks / totalSteps;
            if(ticksPerStep <= 0)
            {
                ticksPerStep = 1;
            }

            // Shrink only when we hit a "step"
            if (shrinkTicksRemaining % ticksPerStep == 0) {
                borderSize = (float) Math.max(targetSize, borderSize - shrinkPerStep);
            }

            changingSize = true;
            shrinkTicksRemaining--;
        } else {
            changingSize = false;
            borderSize = targetSize; // Snap cleanly at end
        }
    }

    public static VoxelShape createShape(Box box) {
        // Convert world coordinates to block-local (0-16 scale)
        double minX = box.minX * 16;
        double minY = box.minY * 16;
        double minZ = box.minZ * 16;
        double maxX = box.maxX * 16;
        double maxY = box.maxY * 16;
        double maxZ = box.maxZ * 16;

        return VoxelShapes.cuboid(
                minX / 16.0, minY / 16.0, minZ / 16.0,
                maxX / 16.0, maxY / 16.0, maxZ / 16.0
        );
    }

    public static VoxelShape createHollowShape(Box box) {
        double minX = box.minX;
        double minY = box.minY;
        double minZ = box.minZ;
        double maxX = box.maxX;
        double maxY = box.maxY;
        double maxZ = box.maxZ;

        double thickness = 0.5;

        VoxelShape shape = VoxelShapes.empty();

        // Left wall
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX, minY, minZ, minX+thickness, maxY, maxZ));
        // Right wall
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(maxX-thickness, minY, minZ, maxX, maxY, maxZ));
        // Front wall
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX+thickness, minY, minZ, maxX-thickness, maxY, minZ+thickness));
        // Back wall
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX+thickness, minY, maxZ-thickness, maxX-thickness, maxY, maxZ));
        // Bottom
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX+thickness, minY, minZ+thickness, maxX-thickness, minY+thickness, maxZ-thickness));
        // Top
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(minX+thickness, maxY-thickness, minZ+thickness, maxX-thickness, maxY, maxZ-thickness));

        return shape;
    }

    public static void handleBorderCollision(LivingEntity entity, Box box) {
        voxelShape = Border.createHollowShape(box);
        borderBox = box;
        if (!changingSize || !box.expand(0.3).contains(entity.getPos())) return;

        if (entity instanceof ServerPlayerEntity serverPlayer) {
            Vec3d pos = entity.getPos();
            Vec3d nextPos = entity.getPos().add(entity.getVelocity());

            double minY = box.minY + 0.5;
            if (!box.contains(nextPos) || !box.expand(0.05).contains(entity.getPos())) {
                double clampedX = MathHelper.clamp(nextPos.x, box.minX + 0.2, box.maxX - 0.2);
                double clampedY = pos.y;
                if (pos.y < minY) {
                    clampedY = minY;
                }
                double clampedZ = MathHelper.clamp(nextPos.z, box.minZ + 0.2, box.maxZ - 0.2);

                serverPlayer.networkHandler.requestTeleport(clampedX, clampedY, clampedZ, entity.getYaw(), entity.getPitch());

                if (entity.getVelocity().y < 0) {
                    entity.setVelocity(entity.getVelocity().x, 0.1, entity.getVelocity().z);
                    entity.velocityModified = true;
                }
            }
        }
    }
}
