package aureum.asta.disks.ports.other;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.lwjgl.system.NonnullDefault;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@NonnullDefault
public final class ReachEntityAttributes implements ModInitializer {
    public static final String MOD_ID = "reach-entity-attributes";
    public static final EntityAttribute REACH = make("reach", 0.0, -1024.0, 1024.0);
    public static final EntityAttribute ATTACK_RANGE = make("attack_range", 0.0, -1024.0, 1024.0);

    public static double getReachDistance(final LivingEntity entity, final double baseReachDistance) {
        EntityAttributeInstance reachDistance = entity.getAttributeInstance(REACH);
        return reachDistance != null ? baseReachDistance + reachDistance.getValue() : baseReachDistance;
    }

    public static double getSquaredReachDistance(final LivingEntity entity, final double sqBaseReachDistance) {
        double reachDistance = getReachDistance(entity, Math.sqrt(sqBaseReachDistance));
        return reachDistance * reachDistance;
    }

    public static double getAttackRange(final LivingEntity entity, final double baseAttackRange) {
        EntityAttributeInstance attackRange = entity.getAttributeInstance(ATTACK_RANGE);
        return attackRange != null ? baseAttackRange + attackRange.getValue() : baseAttackRange;
    }

    public static double getSquaredAttackRange(final LivingEntity entity, final double sqBaseAttackRange) {
        double attackRange = getAttackRange(entity, Math.sqrt(sqBaseAttackRange));
        return attackRange * attackRange;
    }

    public static List<PlayerEntity> getPlayersWithinReach(final World world, final int x, final int y, final int z, final double baseReachDistance) {
        return getPlayersWithinReach(player -> true, world, x, y, z, baseReachDistance);
    }

    public static List<PlayerEntity> getPlayersWithinReach(
            final Predicate<PlayerEntity> viewerPredicate, final World world, final int x, final int y, final int z, final double baseReachDistance
    ) {
        List<PlayerEntity> playersWithinReach = new ArrayList<>(0);

        for (PlayerEntity player : world.getPlayers()) {
            if (viewerPredicate.test(player)) {
                double reach = getReachDistance(player, baseReachDistance);
                double dx = (double)x + 0.5 - player.getX();
                double dy = (double)y + 0.5 - player.getEyeY();
                double dz = (double)z + 0.5 - player.getZ();
                if (dx * dx + dy * dy + dz * dz <= reach * reach) {
                    playersWithinReach.add(player);
                }
            }
        }

        return playersWithinReach;
    }

    public static boolean isWithinAttackRange(final PlayerEntity player, final Entity entity) {
        return player.squaredDistanceTo(entity) <= getSquaredAttackRange(player, 64.0);
    }

    private static EntityAttribute make(final String name, final double base, final double min, final double max) {
        return new ClampedEntityAttribute("attribute.name.generic.reach-entity-attributes." + name, base, min, max).setTracked(true);
    }

    public void onInitialize() {
        Registry.register(Registries.ATTRIBUTE, new Identifier("reach-entity-attributes", "reach"), REACH);
        Registry.register(Registries.ATTRIBUTE, new Identifier("reach-entity-attributes", "attack_range"), ATTACK_RANGE);
    }
}
