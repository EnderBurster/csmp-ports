package aureum.asta.disks.mixin.ports.fireworkfrenzy;

import aureum.asta.disks.ports.other.FireworkFrenzy;
import aureum.asta.disks.entity.DamageCloudEntity;
import aureum.asta.disks.integration.FireworkFrenzyConfig;
import aureum.asta.disks.util.BlastJumper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.FireworkRocketItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkRocketEntityMixin extends ProjectileEntity implements FlyingItemEntity {
	@Shadow @Final private static TrackedData<ItemStack> ITEM;
	@Shadow protected abstract boolean hasExplosionEffects();
	@Shadow public abstract ItemStack getStack();

	@Shadow private @Nullable LivingEntity shooter;

	@Unique public FireworkRocketEntity self = (FireworkRocketEntity) (Object) this;
	@Unique public LivingEntity directTarget;
	@Unique public float blastSize = 2F;
	@Unique public float knockbackAmount = 1F;
	@Unique public int glowingAmount = 0;

	public FireworkRocketEntityMixin(EntityType<? extends ProjectileEntity> type, World world) { super(type, world); }

	@ModifyArg(method = "explode", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/util/math/Box;expand(D)Lnet/minecraft/util/math/Box;"
	))
	private double fireworkfrenzy$blastRadius(double value) {
		NbtCompound tag = dataTracker.get(ITEM).getSubNbt("Fireworks");
		FireworkRocketItem.Type type = FireworkRocketItem.Type.SMALL_BALL;

		if(tag != null) {
			NbtList nbtList = tag.getList("Explosions", NbtElement.COMPOUND_TYPE);

			for(int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbt = nbtList.getCompound(i);

				if(nbt.contains("Type"))
					type = FireworkRocketItem.Type.values()[nbt.getByte("Type")];
				if(nbt.getBoolean("Trail"))
					knockbackAmount += 0.1F;
				if(nbt.getBoolean("Flicker"))
					glowingAmount += 20;
			}
		}

		if(type == FireworkRocketItem.Type.LARGE_BALL)
			blastSize = 5F;
		else if(type == FireworkRocketItem.Type.STAR)
			blastSize = 3F;
		else
			blastSize = 2F;

		return blastSize;
	}

	@Inject(method = "explode", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z",
			ordinal = 1
	), locals = LocalCapture.CAPTURE_FAILSOFT)
	public void fireworkfrenzy$explodePostDamage(CallbackInfo info, float damage, ItemStack stack, NbtCompound tag, NbtList nbtList, double d, Vec3d vec, List<LivingEntity> list, Iterator<LivingEntity> iterator, LivingEntity target) {
		if(FireworkFrenzyConfig.allowRocketJumping && hasExplosionEffects() && tag != null) {
			float radius = blastSize / 2;
			double multiplier = (nbtList.size() * 10) * FireworkFrenzyConfig.rocketJumpMultiplier * knockbackAmount;
			DamageSource source = self.getDamageSources().fireworks(self, getOwner());

			if(!target.blockedByShield(source)) {
				Vec3d targetPos = target.getPos().add(0, MathHelper.clamp(getY() - target.getY(), 0, target.getHeight()), 0);
				Vec3d direction = targetPos.subtract(getPos());
				double distance = Math.max(1, direction.length() - (getWidth() * 0.5) - (target.getWidth() * 0.5));

				double inverseDistance = MathHelper.clamp(1 - (distance / radius), 1, 1.2);
				float fireworkDamage = (target instanceof PlayerEntity ? FireworkFrenzyConfig.playerDamage : FireworkFrenzyConfig.mobDamage) * nbtList.size();

				if(target == getOwner() && EnchantmentHelper.getLevel(FireworkFrenzy.TAKEOFF, target.getEquippedStack(EquipmentSlot.FEET)) > 0)
					fireworkDamage = 0;
				if(EnchantmentHelper.getLevel(FireworkFrenzy.AIR_STRIKE, stack) > 0)
					fireworkDamage *= (float) FireworkFrenzyConfig.airStrikeDamageMultiplier;

				if(glowingAmount > 0)
					target.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, glowingAmount, 0, false, false));

				float rocketDamageFalloffPerMeter = 1.0f;
				float minFalloffMultiplier = 0.3f;
				fireworkDamage = Math.max(minFalloffMultiplier * fireworkDamage, fireworkDamage - Math.max(0, this.distanceTo(getOwner()) - rocketDamageFalloffPerMeter) * rocketDamageFalloffPerMeter);

				if(target == directTarget) {
					target.damage(source, fireworkDamage);
				}
				else {
					target.damage(source, (float) Math.max(1, (fireworkDamage / distance)));
				}

                Vec3d targetPos2 = new Vec3d(target.getX(), target.getY() + (target.getHeight() / 2), target.getZ());
				Vec3d velocityDirection = new Vec3d(target.getX() - getX(), targetPos2.getY() - getY(), target.getZ() - getZ());

				/*double multiplier2 = ((nbtList.size() + (tag.getBoolean("Fireball") ? 1 : 0)) * 0.3) * knockbackAmount * (target == getOwner() ? FireworkFrenzyConfig.rocketJumpMultiplier : FireworkFrenzyConfig.otherEntityKnockBack);
				var targetVelocity = target.getVelocity();
				targetVelocity = new Vec3d(targetVelocity.x, Math.max(1, Math.abs(targetVelocity.y)), targetVelocity.z).multiply(multiplier2 / distance);


				target.setVelocity(targetVelocity.add(direction).multiply(inverseDistance * (target == getOwner() ? FireworkFrenzyConfig.rocketJumpMultiplier : FireworkFrenzyConfig.otherEntityKnockBack)));*/
				//target.knockbackVelocity = 0F;
				/*target.setVelocity(target.getVelocity().x(), Math.min(1D, Math.abs(target.getVelocity().y())), target.getVelocity().z());
				target.setVelocity(target.getVelocity().add(direction).multiply(inverseDistance * (target == getOwner() ? multiplier : multiplier * FireworkFrenzyConfig.otherEntityKnockBack)));*/

				target.setVelocity(target.getVelocity().getX(), Math.abs(target.getVelocity().getY()), target.getVelocity().getZ());
				target.setVelocity(target.getVelocity().add(velocityDirection).multiply(inverseDistance * multiplier));

				target.velocityModified = true;
			}
		}

		if(target instanceof BlastJumper jumper) {
			jumper.setTimeOnGround(0);
			jumper.setBlastJumping(true);
		}
	}

	@Inject(method = "explode", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private void fireworkfrenzy$spawnPotionCloud(CallbackInfo info) {
		ItemStack stack = dataTracker.get(ITEM);
		NbtCompound tag = stack.isEmpty() ? null : stack.getSubNbt("Fireworks");
		FireworkRocketItem.Type type = FireworkRocketItem.Type.SMALL_BALL;

		if(tag != null) {
			NbtList nbtList = tag.getList("Explosions", NbtElement.COMPOUND_TYPE);

			for(int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbt = nbtList.getCompound(i);

				if(nbt.contains("Type"))
					type = FireworkRocketItem.Type.values()[nbt.getByte("Type")];
			}

			if(type == FireworkRocketItem.Type.STAR) {
				DamageCloudEntity cloud = FireworkFrenzy.DAMAGE_CLOUD.create(world);

				if(cloud != null) {
					cloud.setRadius(blastSize);
					cloud.setOwner(shooter);
					cloud.setDuration(200);
					cloud.setColor(0xf8d26a);
					cloud.setPosition(getPos().add(0, -cloud.getRadius(), 0));
					world.spawnEntity(cloud);
				}
			}
		}
	}

	@Inject(method = "onEntityHit", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FireworkRocketEntity;explodeAndRemove()V"))
	public void fireworkfrenzy$directHit(EntityHitResult entityHitResult, CallbackInfo info) {
		if(entityHitResult.getEntity() instanceof LivingEntity target)
			directTarget = target;
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 0))
	public float fireworkfrenzy$selfDamage(DamageSource source, float amount) {
		return 0;
	}

	@ModifyArg(method = "explode", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z", ordinal = 1))
	public float fireworkfrenzy$crossbowDamage(DamageSource source, float amount) {
		return 0;
	}
}
