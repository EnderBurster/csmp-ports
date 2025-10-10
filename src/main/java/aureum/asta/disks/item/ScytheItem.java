package aureum.asta.disks.item;

import aureum.asta.disks.ports.other.ReachEntityAttributes;
import aureum.asta.disks.entity.BloodScytheEntity;
import aureum.asta.disks.index.ArsenalDamageTypes;
import aureum.asta.disks.index.ArsenalEnchantments;
import aureum.asta.disks.index.ArsenalSounds;
import aureum.asta.disks.util.SweepParticleUtil;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.UUID;



public class ScytheItem extends SwordItem implements CustomHitParticleItem, CustomHitSoundItem, Vanishable
{
    private static final EntityAttributeModifier REACH_MODIFIER = new EntityAttributeModifier(
            UUID.fromString("911af262-067d-4da2-854c-20f03cc2dd8b"), "Weapon modifier", 0.5, EntityAttributeModifier.Operation.ADDITION
    );

    public ScytheItem(ToolMaterial material, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
    }


    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        Multimap<EntityAttribute, EntityAttributeModifier> map = LinkedHashMultimap.create(super.getAttributeModifiers(slot));
        if (slot == EquipmentSlot.MAINHAND) {
            map.put(ReachEntityAttributes.ATTACK_RANGE, REACH_MODIFIER);
        }

        return map;
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

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        if (state.isOf(Blocks.COBWEB)) {
            return 1.0F;
        } else if (state.isIn(BlockTags.HOE_MINEABLE)) {
            return 15.0F;
        } else {
            Material material = state.getMaterial();
            return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && !state.isIn(BlockTags.LEAVES) && material != Material.GOURD ? 1.0F : 1.5F;
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        if (EnchantmentHelper.getEquipmentLevel(ArsenalEnchantments.SPEWING, player) <= 0)
        {
            return super.use(world, player, hand);
        }
        else
        {
            float f = 1.0F;
            if (!world.isClient)
            {
                BloodScytheEntity bloodScythe = new BloodScytheEntity(world, player);
                bloodScythe.setOwner(player);
                bloodScythe.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, f * 3.0F, 1.0F);
                bloodScythe.setDamage(bloodScythe.getDamage());
                player.getStackInHand(hand).damage(1, player, p -> p.sendToolBreakStatus(hand));
                bloodScythe.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                ArrayList<StatusEffectInstance> statusEffectsHalved = new ArrayList<>();
                float absorption = player.getAbsorptionAmount();

                for (StatusEffectInstance statusEffect : player.getStatusEffects())
                {
                    StatusEffectInstance statusHalved = new StatusEffectInstance(
                            statusEffect.getEffectType(),
                            statusEffect.getDuration() /2,
                            statusEffect.getAmplifier(),
                            statusEffect.isAmbient(),
                            statusEffect.shouldShowParticles(),
                            statusEffect.shouldShowIcon()
                    );
                    bloodScythe.addEffect(statusHalved);
                    statusEffectsHalved.add(statusHalved);
                }

                player.clearStatusEffects();

                for (StatusEffectInstance statusEffectInstance : statusEffectsHalved)
                {
                    player.addStatusEffect(statusEffectInstance);
                }

                player.setAbsorptionAmount(absorption);
                player.damage(world.getDamageSources().create(ArsenalDamageTypes.SPEWING), 3.0F);
                player.getItemCooldownManager().set(this, 20);
                world.spawnEntity(bloodScythe);

                if (player.getWorld() instanceof ServerWorld serverWorld)
                {
                    ScytheItem.Skin skin = Skin.DEFAULT;
                    Pair<Integer, Integer> colorPair = new Pair<>(skin.color, skin.shadowColor);
                    SweepParticleUtil.sendSweepPacketToClient(
                            serverWorld,
                            colorPair,
                            player.getX() + (double)(-MathHelper.sin((float)((double)player.getYaw() * (Math.PI / 180)))),
                            player.getBodyY(0.5),
                            player.getZ() + (double)MathHelper.cos((float)((double)player.getYaw() * (Math.PI / 180.0)))
                    );
                }
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(), ArsenalSounds.ITEM_SCYTHE_SPEWING, SoundCategory.PLAYERS, 1.0F, 1.0F);
            return TypedActionResult.success(player.getStackInHand(hand));
        }
    }

    public void spawnHitParticles(PlayerEntity player)
    {
        if (player.getWorld() instanceof ServerWorld serverWorld)
        {
            ScytheItem.Skin skin = Skin.DEFAULT;
            Pair<Integer, Integer> colorPair = new Pair(skin.color, skin.shadowColor);
            SweepParticleUtil.sendSweepPacketToClient(
                    serverWorld,
                    colorPair,
                    player.getX() + (double) (-MathHelper.sin((float)((double)player.getYaw() * (Math.PI / 180.0)))),
                    player.getBodyY(0.5),
                    player.getZ() + (double)MathHelper.cos((float)((double)player.getYaw() * (Math.PI / 180.0)))
            );
        }
    }

    public void playHitSound(PlayerEntity player)
    {
        player.playSound(ArsenalSounds.ITEM_SCYTHE_HIT, 1.0F, (float)(1.0 + player.getRandom().nextGaussian() / 10.0));
    }

    public static enum Skin
    {
        DEFAULT(-2554848, -7601120);

        public final int color;
        public final int shadowColor;

        private Skin(int color, int shadowColor)
        {
            this.color = color;
            this.shadowColor = shadowColor;
        }
    }
}
