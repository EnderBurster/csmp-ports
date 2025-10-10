package aureum.asta.disks.cca.entity;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.client.AureumAstaDisksClient;
import aureum.asta.disks.client.particle.effect.SparkParticleEffect;
import aureum.asta.disks.client.sound.SparkSoundInstance;
import aureum.asta.disks.init.AstaEnchantments;
import aureum.asta.disks.init.AstaEntityComponents;
import aureum.asta.disks.packets.ThunderstruckPacket;
import aureum.asta.disks.packets.UseThunderstruckPacket;
import aureum.asta.disks.ports.mace.FaithfulMace;
import aureum.asta.disks.ports.mace.item.MaceItem;
import aureum.asta.disks.sound.AstaSounds;
import aureum.asta.disks.util.EnchantingClientUtil;
import aureum.asta.disks.util.EnchantingUtil;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import moriyashiine.enchancement.common.util.EnchancementUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;

import java.util.List;

public class ThunderstruckComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final LivingEntity obj;
    private boolean using = false;
    private double cachedHeight = 0;
    private int floatTicks = 0, smashTicks = 0;
    private int chargeTime = 10;
    private int floatTime = 60;
    private float lungeStrength = 0.8F;
    private int smashStrength = 1;
    private final float smashDamageMultiplier = 1.6F;
    private boolean hasThunderstruck = false;

    private boolean playedSound = false;
    private int ticksUsing = 0;

    private float nextTickFallDistance = 0;

    public ThunderstruckComponent(LivingEntity obj) {
        this.obj = obj;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        using = tag.getBoolean("Using");
        floatTicks = tag.getInt("FloatTicks");
        smashTicks = tag.getInt("SmashTicks");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean("Using", using);
        tag.putInt("FloatTicks", floatTicks);
        tag.putInt("SmashTicks", smashTicks);
    }

    @Override
    public void tick() {
        int thunderstruckLevel = EnchantmentHelper.getLevel(AstaEnchantments.THUNDERSTRUCK, obj.getMainHandStack());
        hasThunderstruck = thunderstruckLevel > 0;

        if (!hasThunderstruck)
        {
            cancel();
            return;
        }


        int chargeTime = this.chargeTime;
        if (isFloating() || isSmashing()) {
            if (chargeTime == 0) {
                cancel();
            }
        }
        if (isFloating()) {
            floatTicks--;
            obj.onLanding();
            obj.setVelocity(obj.getVelocity().multiply(0.9));
            if (obj.handSwinging && obj.getPitch() > -15) {
                cachedHeight = obj.getY();
                smashTicks = 30;
                floatTicks = 0;
                obj.setVelocity(obj.getRotationVector().multiply(smashStrength));
                obj.playSound(AstaSounds.ENTITY_GENERIC_ZAP, 2, 1);
            }
        }
        if (isSmashing()) {
            smashTicks--;
            if (obj.isOnGround()) {
                if (smashTicks > 1) {
                    smashTicks = 1;
                }
                if (smashTicks == 1) {
                    obj.playSound(FaithfulMace.ITEM_MACE_SMASH_GROUND_HEAVY_SOUND_EVENT, 1, 1);
                    obj.getWorld().emitGameEvent(GameEvent.STEP, obj.getPos(), GameEvent.Emitter.of(obj.getSteppingBlockState()));
                }
            }
        }
        if (chargeTime > 0 && ItemStack.areEqual(obj.getActiveItem(), obj.getMainHandStack())) {
            if (!playedSound && obj.getItemUseTime() == chargeTime) {
                obj.playSound(AstaSounds.ENTITY_GENERIC_PING, 1, 1);
                playedSound = true;
            }
            if (ticksUsing % 18 == 0) {
                obj.playSound(AstaSounds.ITEM_GENERIC_WHOOSH, 0.5F, 1);
            }
            ticksUsing++;
        } else {
            playedSound = false;
            ticksUsing = 0;
        }
        if (nextTickFallDistance != 0) {
            obj.handleFallDamage(nextTickFallDistance, 1, obj.getDamageSources().fall());
            nextTickFallDistance = 0;
        }
    }

    @Override
    public void serverTick() {
        tick();
        if (isFloating() && !EnchantingUtil.isSufficientlyHigh(obj, 0.25)) {
            cancel();
            sync();
        }
        if (smashTicks == 1 && obj.isOnGround()) {
            ServerWorld serverWorld = (ServerWorld) obj.getWorld();

            PlayerLookup.tracking(obj).forEach(player -> ThunderstruckPacket.send(player, obj));

            obj.fallDistance = (float) Math.max(0, cachedHeight - obj.getY());
            float base = (float) obj.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
            boolean[] hurt = {true};
            getNearby(3).forEach(entity -> {
                DamageSource source;
                if (obj instanceof PlayerEntity player) {
                    source = serverWorld.getDamageSources().playerAttack(player);
                } else {
                    source = serverWorld.getDamageSources().mobAttack(obj);
                }

                float damage = EnchantmentHelper.getAttackDamage(obj.getMainHandStack(), entity.getGroup()) + obj.getMainHandStack().getItem().getBonusAttackDamage(entity, base, source);
                //float damage = EnchantmentHelper.getDamage(serverWorld, obj.getMainHandStack(), entity, source, base) + obj.getMainHandStack().getItem().getBonusAttackDamage(entity, base, source);

                if (entity.damage(source, damage * smashDamageMultiplier)) {
                    entity.takeKnockback(1.5F, obj.getX() - entity.getX(), obj.getZ() - entity.getZ());
                    hurt[0] = false;
                }

                for (ServerPlayerEntity serverPlayer : serverWorld.getPlayers()) {

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeDouble(entity.getX());
                    buf.writeDouble(entity.getY() + 1.0);
                    buf.writeDouble(entity.getZ());

                    ServerPlayNetworking.send(serverPlayer, AureumAstaDisksClient.SPAWN_PARTICLE_PACKET_ID, buf);
                }
            });
            if (hurt[0] && !obj.isTouchingWater()) {
                nextTickFallDistance = obj.fallDistance;
            }
            obj.fallDistance = 0;
        }
    }

    @Override
    public void clientTick() {
        tick();
        if (isFloating() && EnchantingClientUtil.shouldAddParticles(obj)) {
            for (int i = 0; i <= 4; i++) {
                obj.getWorld().addParticle(new SparkParticleEffect(obj.getPos().addRandom(obj.getRandom(), 1)), obj.getParticleX(1), obj.getRandomBodyY(), obj.getParticleZ(1), 0, 0, 0);
            }
        }
        if (smashTicks == 1 && obj.isOnGround()) {
            ThunderstruckPacket.addParticles(obj);
        }
    }

    public void sync() {
        AstaEntityComponents.THUNDERSTRUCK.sync(obj);
    }

    public void setUsing(boolean using) {
        this.using = using;
    }

    public boolean isUsing() {
        return using;
    }

    public void useCommon(Vec3d lungeVelocity, int floatTicks) {
        obj.setVelocity(lungeVelocity);
        this.floatTicks = floatTicks;
    }

    @Environment(EnvType.CLIENT)
    public void useClient() {
        MinecraftClient.getInstance().getSoundManager().play(new SparkSoundInstance(obj));
    }

    public void useServer(Vec3d lungeVelocity, int floatTicks) {
        PlayerLookup.tracking(obj).forEach(foundPlayer -> UseThunderstruckPacket.send(foundPlayer, obj, lungeVelocity, floatTicks));
    }

    public boolean isFloating() {
        return floatTicks > 0;
    }

    public boolean isSmashing() {
        return smashTicks > 0;
    }

    public void cancel() {
        floatTicks = smashTicks = 0;
    }

    @SuppressWarnings("SameParameterValue")
        private List<LivingEntity> getNearby(int range) {
        return obj.getWorld().getEntitiesByClass(LivingEntity.class,
                new Box(
                        obj.getX() - 0.5 - range, obj.getY() - 1.5, obj.getZ() - 0.5 - range,
                        obj.getX() + 0.5 + range, obj.getY() + 0.5 + range, obj.getZ() + 0.5 + range
                ), foundEntity -> foundEntity.isAlive() && foundEntity.distanceTo(obj) < 10 && EnchancementUtil.shouldHurt(obj, foundEntity) && EnchantingUtil.canSee(obj, foundEntity, range));
    }

    public int getFloatTime() {
        return floatTime;
    }

    public int getChargeTime() {
        return chargeTime;
    }

    public float getLungeStrength() {
        return lungeStrength;
    }

    public boolean hasThunderstruck() {
        return hasThunderstruck;
    }
}
