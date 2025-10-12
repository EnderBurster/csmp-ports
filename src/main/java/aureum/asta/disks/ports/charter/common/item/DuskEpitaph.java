package aureum.asta.disks.ports.charter.common.item;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.interfaces.BackslotExtraLarge;
import aureum.asta.disks.interfaces.Dualhanded;
import aureum.asta.disks.ports.charter.Charter;
import aureum.asta.disks.ports.charter.common.component.CharterComponents;
import aureum.asta.disks.ports.charter.common.damage.CharterDamageSources;
import aureum.asta.disks.ports.charter.common.entity.EpitaphChainsEntity;
import aureum.asta.disks.ports.charter.common.entity.EpitaphShockwaveEntity;
import aureum.asta.disks.ports.other.ReachEntityAttributes;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.api.layered.modifier.AbstractFadeModifier;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.UUID;

public class DuskEpitaph extends SwordItem implements Dualhanded, BackslotExtraLarge {
    private static final EntityAttributeModifier REACH_MODIFIER = new EntityAttributeModifier(
            UUID.fromString("911af262-067d-4da2-854c-20f03cc2dd8b"), "Weapon modifier", 0.5, EntityAttributeModifier.Operation.ADDITION
    );

    public DuskEpitaph(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
        super(toolMaterial, attackDamage, attackSpeed, settings);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> map = LinkedHashMultimap.create(super.getAttributeModifiers(slot));
        if (slot == EquipmentSlot.MAINHAND) {
            map.put(ReachEntityAttributes.ATTACK_RANGE, REACH_MODIFIER);
        }

        return map;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        Entity entity = raycastEntity(world, user, 5.0f);

        /*if (entity instanceof LivingEntity living) {
            /*if(world.isClient)
            {
                ClientEpitaph.playTestAnimation(user);
            }

            if(!world.isClient)
            {
                Charter.sendAnimationToTracking((ServerPlayerEntity) user);
            }

            user.getComponent(CharterComponents.PLAYER_COMPONENT).setEpitaphBanning(true);

            EpitaphShockwaveEntity shockwave = new EpitaphShockwaveEntity(living.getWorld());
            shockwave.requestTeleport(living.getX(), living.getY(), living.getZ());
            living.getWorld().spawnEntity(shockwave);
        }*/

        if ((entity instanceof EpitaphChainsEntity) || (entity instanceof PlayerEntity player && player.getVehicle() instanceof EpitaphChainsEntity)) {
            EpitaphChainsEntity chains;
            PlayerEntity targetPlayer;

            if(entity instanceof EpitaphChainsEntity)
            {
                chains = (EpitaphChainsEntity) entity;
                targetPlayer = (PlayerEntity) chains.getFirstPassenger();
            }
            else
            {
                targetPlayer = (PlayerEntity) entity;
                chains = (EpitaphChainsEntity) entity.getVehicle();
            }

            /*if(world.isClient)
            {
                ClientEpitaph.playTestAnimation(user);
            }*/

            if(!world.isClient)
            {
                Charter.sendAnimationToTracking((ServerPlayerEntity) user);
            }

            user.getComponent(CharterComponents.PLAYER_COMPONENT).setEpitaphBanning(true);

            EpitaphShockwaveEntity shockwave = new EpitaphShockwaveEntity(targetPlayer.getWorld());
            shockwave.requestTeleport(chains.getX(), chains.getY(), chains.getZ());
            shockwave.setBannedPlayer((PlayerEntity) chains.getFirstPassenger());
            targetPlayer.getWorld().spawnEntity(shockwave);
        }

        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public static Entity raycastEntity(World world, PlayerEntity player, double distance) {
        Vec3d eyePos = player.getCameraPosVec(1.0F);
        Vec3d lookVec = player.getRotationVec(1.0F);
        Vec3d endVec = eyePos.add(lookVec.multiply(distance));

        Box box = player.getBoundingBox().stretch(lookVec.multiply(distance)).expand(1.0D, 1.0D, 1.0D);

        EntityHitResult entityHitResult = ProjectileUtil.getEntityCollision(
                player.world,
                player,
                eyePos,
                endVec,
                box,
                entity -> !entity.isSpectator() && entity.canHit()
        );

        AureumAstaDisks.LOGGER.info("Entity hit result: {}", entityHitResult);

        if (entityHitResult != null) {
            Entity target = entityHitResult.getEntity();
            AureumAstaDisks.LOGGER.info("Entity hit target: {}", target);
            return target;
        }

        return null;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!(entity instanceof LivingEntity living)) return;

        var attributes = living.getAttributes();
        Multimap<EntityAttribute, EntityAttributeModifier> map = LinkedHashMultimap.create();
        map.put(ReachEntityAttributes.REACH, REACH_MODIFIER);

        if (selected) {
            if (!attributes.hasModifierForAttribute(ReachEntityAttributes.ATTACK_RANGE, REACH_MODIFIER.getId())) {
                attributes.addTemporaryModifiers(map);
            }
        } else {
            if (attributes.hasModifierForAttribute(ReachEntityAttributes.ATTACK_RANGE, REACH_MODIFIER.getId())) {
                attributes.removeModifiers(map);
            }
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }

    @Override
    public DamageSource mialib$setDamageSource(LivingEntity entity, DamageSources sources) {
        if(entity instanceof PlayerEntity)
        {
            return sources.create(CharterDamageSources.EPITAPH, entity);
        }
        return null;
    }

    @Environment(EnvType.CLIENT)
    public static class ClientEpitaph
    {
        public static void playTestAnimation(PlayerEntity player) {
            if(!(player instanceof ClientPlayerEntity)) return;
            ModifierLayer<IAnimation> testAnimation = (ModifierLayer<IAnimation>) PlayerAnimationAccess.getPlayerAssociatedData((ClientPlayerEntity)player).get(new Identifier("charter", "animation"));

            testAnimation.replaceAnimationWithFade(AbstractFadeModifier.functionalFadeIn(20, (modelName, type, value) -> value),
                    new KeyframeAnimationPlayer(PlayerAnimationRegistry.getAnimation(new Identifier("charter", "animation.player.epitaph"))).setFirstPersonMode(FirstPersonMode.THIRD_PERSON_MODEL).setFirstPersonConfiguration(new FirstPersonConfiguration().setShowRightArm(true).setShowLeftItem(false))
            );

        }
    }
}
