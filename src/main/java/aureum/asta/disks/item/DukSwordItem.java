package aureum.asta.disks.item;

import aureum.asta.disks.index.ArsenalSounds;
import aureum.asta.disks.interfaces.BackslotExtraLarge;
import aureum.asta.disks.interfaces.Dualhanded;
import aureum.asta.disks.ports.other.ReachEntityAttributes;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import moriyashiine.enchancement.common.init.ModEnchantments;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Random;
import java.util.UUID;

public class DukSwordItem extends SwordItem implements Dualhanded, BackslotExtraLarge {
    private static final EntityAttributeModifier REACH_MODIFIER = new EntityAttributeModifier(
            UUID.fromString("911af262-067d-4da2-854c-20f03cc2dd8b"), "Weapon modifier", 0.5, EntityAttributeModifier.Operation.ADDITION
    );

    public DukSwordItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Settings settings) {
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

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getStackInHand(hand);

        if (!world.isClient) {
            if(player.isSneaking())
            {
                stack.removeSubNbt("Enchantments");
                return TypedActionResult.success(stack, world.isClient());
            }

            if (stack.hasEnchantments() && stack.getEnchantments().toString().contains("enchancement:frostbite")) {
                stack.removeSubNbt("Enchantments");
                stack.addEnchantment(Enchantments.FIRE_ASPECT, 5);

                for (int i = 0; i < 15; i++) {
                    world.addParticle(ParticleTypes.FLAME, player.getX(), player.getBodyY(0.5), player.getZ(), 0, 0, 0);
                }

            } else {
                stack.removeSubNbt("Enchantments");
                stack.addEnchantment(ModEnchantments.FROSTBITE, 5);

                for (int i = 0; i < 15; i++) {
                    world.addParticle(ParticleTypes.SNOWFLAKE, player.getX(), player.getBodyY(0.5), player.getZ(), 0, 0, 0);
                }
            }

            player.getItemCooldownManager().set(stack.getItem(), 20);
            world.playSound(null, player.getBlockPos() ,ArsenalSounds.ITEM_SCYTHE_HIT, SoundCategory.PLAYERS);
        }
        else if(!player.isSneaking())
        {
            Random random = new Random();

            if (stack.hasEnchantments() && stack.getEnchantments().toString().contains("enchancement:frostbite")) {
                for (int i = 0; i < 50; i++) {
                    world.addParticle(ParticleTypes.FLAME,
                            player.getX() + random.nextFloat(-0.1f, 0.1f) + player.getVelocity().getX() * 2,
                            player.getEyeY() - 0.1 + random.nextFloat(-0.1f, 0.1f) + player.getVelocity().getY() * 2,
                            player.getZ() + random.nextFloat(-0.1f, 0.1f) + player.getVelocity().getZ() * 2,
                            player.getVelocity().getX() + random.nextFloat(-0.1f, 0.1f),
                            player.getVelocity().getY() + random.nextFloat(-0.1f, -0.01f),
                            player.getVelocity().getZ() + random.nextFloat(-0.1f, 0.1f));
                }

            } else {
                for (int i = 0; i < 50; i++) {
                    world.addParticle(ParticleTypes.SNOWFLAKE,
                            player.getX() + random.nextFloat(-0.1f, 0.1f) + player.getVelocity().getX() * 2,
                            player.getEyeY() - 0.1 + random.nextFloat(-0.1f, 0.1f) + player.getVelocity().getY() * 2,
                            player.getZ() + random.nextFloat(-0.1f, 0.1f) + player.getVelocity().getZ() * 2,
                            player.getVelocity().getX() + random.nextFloat(-0.1f, 0.1f),
                            player.getVelocity().getY() + random.nextFloat(-0.1f, -0.01f),
                            player.getVelocity().getZ() + random.nextFloat(-0.1f, 0.1f));
                }
            }
        }

        return TypedActionResult.success(stack, world.isClient());
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return false;
    }
}
