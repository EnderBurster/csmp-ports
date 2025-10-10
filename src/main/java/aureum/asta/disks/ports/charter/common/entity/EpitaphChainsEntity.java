package aureum.asta.disks.ports.charter.common.entity;

import aureum.asta.disks.ports.charter.common.init.CharterEntities;
import aureum.asta.disks.ports.charter.common.interfaces.LockedTransport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class EpitaphChainsEntity extends Entity implements LockedTransport {
    @Nullable
    public UUID ownerUuid;
    public final boolean temp;

    public EpitaphChainsEntity(EntityType<? extends EpitaphChainsEntity> type, World world) {
        super(type, world);
        this.intersectionChecked = true;
        this.temp = false;
        this.ownerUuid = null;
    }

    public EpitaphChainsEntity(World world) {
        super(CharterEntities.EPITAPH_CHAINS, world);
        this.intersectionChecked = true;
        this.temp = false;
        this.ownerUuid = null;
    }

    public EpitaphChainsEntity(World world, @Nullable UUID uuid) {
        super(CharterEntities.EPITAPH_CHAINS, world);
        this.intersectionChecked = true;
        this.temp = false;
        this.ownerUuid = uuid;
    }

    public EpitaphChainsEntity(World world, @Nullable UUID uuid, boolean temporary) {
        super(CharterEntities.EPITAPH_CHAINS, world);
        this.intersectionChecked = true;
        this.temp = temporary;
        this.ownerUuid = uuid;
    }

    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.world.isClient && !this.isRemoved()) {
            this.scheduleVelocityUpdate();
            this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
            if (source.getAttacker() instanceof PlayerEntity player && this.ownerUuid != null && player.getUuid().equals(this.ownerUuid)
                    || source.isSourceCreativePlayer()) {
                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }

    /*public ActionResult interact(PlayerEntity player, Hand hand) {
        if (player.getUuid().equals(this.ownerUuid) && this.getFirstPassenger() instanceof PlayerEntity bound) {
            player.openHandledScreen(new PlayerInventoryHandler(bound));
            this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
            player.swingHand(hand, this.world.isClient);
        }

        return super.interact(player, hand);
    }*/

    public boolean canBeMovedBy(PlayerEntity player) {
        return player.getUuid().equals(this.ownerUuid);
    }

    public boolean isInvulnerable() {
        return true;
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return damageSource.isOf(DamageTypes.OUT_OF_WORLD);
    }

    protected void initDataTracker() {
    }

    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.ownerUuid = nbt.getUuid("owner");
    }

    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putUuid("owner", this.ownerUuid);
    }

    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    protected Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
        return LivingEntity.positionInPortal(super.positionInPortal(portalAxis, portalRect));
    }

    public boolean canHit() {
        return !this.isRemoved();
    }

    public void updatePassengerPosition(Entity passenger) {
        if (this.hasPassenger(passenger)) {
            passenger.setPosition(this.getX(), this.getY() + 0.01F, this.getZ());
            passenger.setYaw(MathHelper.clamp(passenger.getYaw(), this.getYaw() - 45.0F, this.getYaw() + 45.0F));
            passenger.setPitch(MathHelper.clamp(passenger.getPitch(), this.getPitch() - 45.0F, this.getPitch() + 45.0F));
            passenger.setHeadYaw(MathHelper.clamp(passenger.getHeadYaw(), this.getYaw() - 45.0F, this.getYaw() + 45.0F));
            if (passenger instanceof LivingEntity l) {
                passenger.setBodyYaw(MathHelper.clamp(l.bodyYaw, this.getYaw() - 10.0F, this.getYaw() + 10.0F));
            }
        }
    }

    public void tick() {
        super.tick();
        if (!this.hasPassengers() || this.temp && this.age >= 1200) {
            this.remove(RemovalReason.DISCARDED);
        }
    }

    public boolean hasNoGravity() {
        return true;
    }
}
