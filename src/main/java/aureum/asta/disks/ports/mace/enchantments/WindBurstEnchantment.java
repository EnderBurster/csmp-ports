package aureum.asta.disks.ports.mace.enchantments;

import aureum.asta.disks.ports.mace.ExplosionUtil;
import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.entity.WindChargeNoDamageEntitiesExplosionBehavior;
import aureum.asta.disks.ports.mace.item.MaceItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class WindBurstEnchantment extends Enchantment {
    protected WindBurstEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    public void onTargetDamagedCustomLocation(LivingEntity user, int level, float fallDist) {
        if (user.getWorld() instanceof ServerWorld) {
            if (!((double)fallDist < (double)1.5F)) {
                if (user instanceof LivingEntity) {
                    if (user.isFallFlying()) {
                        return;
                    }

                    if (user instanceof PlayerEntity) {
                        PlayerEntity playerEntity = (PlayerEntity)user;
                        if (playerEntity.getAbilities().flying) {
                            return;
                        }
                    }
                }

                boolean attributeToUser = false;
                DamageSource damageSource = null;
                float var10000;
                switch (level) {
                    case 1 -> var10000 = 1.2F;
                    case 2 -> var10000 = 1.75F;
                    case 3 -> var10000 = 2.2F;
                    default -> var10000 = 1.5F + (float)(level - 1) * 0.35F;
                }

                float knockbackMultiplier = var10000;
                Vec3d pos = user.getPos();
                Vec3d offset = Vec3d.ZERO;
                float radius = 3.5F;
                boolean createFire = false;
                World.ExplosionSourceType blockInteraction = World.ExplosionSourceType.NONE;
                ParticleEffect smallParticle = FaithfulMace.GUST_EMITTER_SMALL;
                ParticleEffect largeParticle = FaithfulMace.GUST_EMITTER_LARGE;
                RegistryEntry<SoundEvent> sound = Registries.SOUND_EVENT.getEntry(FaithfulMace.ENTITY_WIND_CHARGE_WIND_BURST_SOUND_EVENT);
                Vec3d vec3d = pos.add(offset);
                ExplosionUtil.createExplosion((ServerWorld)user.getWorld(), attributeToUser ? user : null, damageSource, new WindChargeNoDamageEntitiesExplosionBehavior(knockbackMultiplier), vec3d.getX(), vec3d.getY(), vec3d.getZ(), radius, createFire, blockInteraction, smallParticle, largeParticle, sound);
            }
        }
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof MaceItem;
    }

    public int getMinPower(int level) {
        return 1;
    }

    public int getMaxLevel() {
        return 3;
    }
}
