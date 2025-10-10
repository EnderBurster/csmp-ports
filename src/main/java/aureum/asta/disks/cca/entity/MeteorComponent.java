package aureum.asta.disks.cca.entity;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.init.AstaEnchantments;
import aureum.asta.disks.packets.MeteorPacket;
import aureum.asta.disks.sound.AstaSounds;
import aureum.asta.disks.util.EnchantingUtil;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import moriyashiine.enchancement.common.init.ModSoundEvents;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public class MeteorComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final int chargeTime = 10;
    private final float jumpStrength = 1.35F;
    private final int fireDuration = 8;
    private boolean hasMeteor = false;
    private final LivingEntity obj;
    private boolean using = false;

    private boolean playedSound = false;

    public MeteorComponent(LivingEntity obj) {
        this.obj = obj;
    }

    public void readFromNbt(NbtCompound tag) {
        using = tag.getBoolean("Using");
    }

    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("Using", using);
    }

    public int getFireDuration() {
        return fireDuration;
    }

    public float getJumpStrength() {
        return jumpStrength;
    }

    public int getChargeTime() {
        return chargeTime;
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    public boolean isUsing() {
        return using;
    }

    public boolean hasMeteor() {
        return hasMeteor;
    }

    @Override
    public void tick() {
        int meteorLevel = EnchantmentHelper.getLevel(AstaEnchantments.METEOR, obj.getMainHandStack());
        hasMeteor = meteorLevel > 0;

        if (!hasMeteor)
        {
            return;
        }

        int chargeTime = this.chargeTime;
        if (chargeTime > 0 && ItemStack.areEqual(obj.getActiveItem(), obj.getMainHandStack())) {
            if (!playedSound && obj.getItemUseTime() == chargeTime) {
                obj.playSound(AstaSounds.ENTITY_GENERIC_PING, 1, 1);
                playedSound = true;
            }
        } else {
            playedSound = false;
        }
    }

    public void useCommon() {
        obj.setVelocity(obj.getVelocity().getX(),this.jumpStrength, obj.getVelocity().getZ());
        obj.playSound(AstaSounds.ENTITY_GENERIC_ERUPT, 1, MathHelper.nextFloat(obj.getRandom(), 0.8F, 1.2F));
    }

    public void useClient() {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        double y = Math.round(obj.getY() - 1);
        for (int i = 0; i < 360; i += 15) {
            for (int j = 1; j < 4; j++) {
                double x = obj.getX() + MathHelper.sin(i) * j / 2, z = obj.getZ() + MathHelper.cos(i) * j / 2;
                BlockState state = obj.getWorld().getBlockState(mutable.set(x, y, z));
                if (!state.isReplaceable() && obj.getWorld().getBlockState(mutable.move(Direction.UP)).isReplaceable()) {
                    BlockStateParticleEffect particle = new BlockStateParticleEffect(ParticleTypes.BLOCK, state);
                    for (int k = 0; k < 2; k++) {
                        obj.getWorld().addParticle(particle, x, mutable.getY() + 0.5, z, 0, 0.5, 0);
                        obj.getWorld().addParticle(ParticleTypes.LAVA, x, mutable.getY() + 0.5, z, 0, 2, 0);
                    }
                }
            }
        }
    }

    public void useServer() {
        ServerWorld serverWorld = (ServerWorld) obj.getWorld();
        PlayerLookup.tracking(obj).forEach(foundPlayer -> MeteorPacket.send(foundPlayer, obj));

        float base = (float) obj.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float fireDuration = this.fireDuration;

        getNearby(obj).forEach(entity -> {
            DamageSource source;
            if (obj instanceof PlayerEntity) {
                source = serverWorld.getDamageSources().playerAttack((PlayerEntity) obj);
            } else {
                source = serverWorld.getDamageSources().mobAttack(obj);
            }

            float damage = EnchantmentHelper.getAttackDamage(obj.getMainHandStack(), entity.getGroup()) + obj.getMainHandStack().getItem().getBonusAttackDamage(entity, base, source);

            entity.setOnFireFor((int) fireDuration);

            if (entity.damage(source, damage)) {
                entity.takeKnockback(1.0F, obj.getX() - entity.getX(), obj.getZ() - entity.getZ());
            }
        });
    }

    private static List<LivingEntity> getNearby(LivingEntity living) {
        return living.getWorld().getEntitiesByClass(LivingEntity.class, new Box(living.getBlockPos()).expand(2, 2, 2), foundEntity ->
                foundEntity.isAlive() && foundEntity.distanceTo(living) < 10 && EnchancementUtil.shouldHurt(living, foundEntity) && EnchantingUtil.canSee(living, foundEntity, 2));
    }
}
