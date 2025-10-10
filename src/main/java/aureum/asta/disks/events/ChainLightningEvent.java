package aureum.asta.disks.events;

import aureum.asta.disks.client.particle.effect.SparkParticleEffect;
import aureum.asta.disks.init.AstaEnchantments;
import aureum.asta.disks.sound.AstaSounds;
import moriyashiine.enchancement.common.init.ModEnchantments;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ChainLightningEvent implements ServerLivingEntityEvents.AllowDamage{
	private static final ThreadLocal<Boolean> FIRST = ThreadLocal.withInitial(() -> true);

	public boolean allowDamage(LivingEntity entity, DamageSource source, float amount) {
		if (FIRST.get()) {
			float multiplier = 0;
			if (source.getAttacker() instanceof LivingEntity living) {
				multiplier = EnchancementUtil.hasEnchantment(Enchantments.CHANNELING, living) || EnchancementUtil.hasEnchantment(AstaEnchantments.THUNDERSTRUCK, living) ? 0.75F : 0;
			} else if (source.getSource() instanceof PersistentProjectileEntity projectile && projectile.getOwner() instanceof LivingEntity living) {
				multiplier = EnchancementUtil.hasEnchantment(Enchantments.CHANNELING, living) || EnchancementUtil.hasEnchantment(AstaEnchantments.THUNDERSTRUCK, living) ? 0.75F : 0;
			}

			if (multiplier != 0) {
				FIRST.set(false);
				chain(new ArrayList<>(), (ServerWorld) entity.getWorld(), entity, source, amount, multiplier);
				FIRST.set(true);
			}
		}
		return true;
	}

	private static void chain(List<LivingEntity> hitEntities, ServerWorld world, LivingEntity target, DamageSource source, float damage, float multiplier) {
		if (damage > 1 && !hitEntities.contains(target)) {
			hitEntities.add(target);
			getNearest(hitEntities, target, source.getSource()).ifPresent(nearest -> {
				target.playSound(AstaSounds.ENTITY_GENERIC_ZAP, 1.0f, 1.0f);
				world.spawnParticles(new SparkParticleEffect(nearest.getEyePos()), target.getX(), target.getEyeY(), target.getZ(), 1, 0, 0, 0, 0);
				Vec3d random = target.getEyePos().addRandom(target.getRandom(), 1.5F);
				world.spawnParticles(new SparkParticleEffect(target.getEyePos()), random.getX(), random.getY(), random.getZ(), 1, 0, 0, 0, 0);
				nearest.damage(source, damage * multiplier);
				chain(hitEntities, world, nearest, source, damage * multiplier, multiplier);
			});
		}
	}

	private static Optional<LivingEntity> getNearest(List<LivingEntity> hitEntities, LivingEntity target, Entity attacker) {
		if (attacker == null) {
			return Optional.empty();
		}
		List<LivingEntity> nearby = target.getWorld().getEntitiesByClass(LivingEntity.class,
			target.getBoundingBox().expand(3, 1, 3),
			foundEntity -> !hitEntities.contains(foundEntity)
				&& foundEntity.distanceTo(attacker) < 8
				&& !(foundEntity instanceof PlayerEntity)
				&& EnchancementUtil.shouldHurt(attacker, foundEntity))
			.stream()
			.sorted(Comparator.comparingDouble(e -> e.distanceTo(attacker)))
			.toList();
		return nearby.isEmpty() ? Optional.empty() : Optional.of(nearby.get(0));
	}
}