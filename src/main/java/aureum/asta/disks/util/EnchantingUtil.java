package aureum.asta.disks.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;

import static moriyashiine.enchancement.common.util.EnchancementUtil.getModifiedMaxLevel;

public class EnchantingUtil {

    public static final Object2IntMap<Enchantment> ORIGINAL_MAX_LEVELS = new Object2IntOpenHashMap<>();

    public static int getOriginalMaxLevel(Enchantment enchantment) {
        return ORIGINAL_MAX_LEVELS.getOrDefault(enchantment, enchantment.getMaxLevel());
    }

    public static int alterLevel(ItemStack stack, Enchantment enchantment, int additionalMax) {
        return getModifiedMaxLevel(stack, getOriginalMaxLevel(enchantment) + additionalMax);
    }

    public static int alterLevel(ItemStack stack, Enchantment enchantment) {
        return alterLevel(stack, enchantment, 0);
    }

    public static boolean canSee(Entity host, Entity target, int range) {
        if (target.getWorld() == host.getWorld() && host.getPos().distanceTo(target.getPos()) <= 32) {
            for (int i = -range; i <= range; i++) {
                if (host.getWorld().raycast(new RaycastContext(host.getPos().add(0, i, 0), target.getPos().add(0, i, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, host)).getType() == HitResult.Type.MISS) {
                    return true;
                }
            }
        }
        return false;
    }
    public static boolean isSufficientlyHigh(Entity entity, double distanceFromGround) {
        return entity.getWorld().raycast(new RaycastContext(entity.getPos(), entity.getPos().add(0, -distanceFromGround, 0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.ANY, entity)).getType() == HitResult.Type.MISS;
    }

}
