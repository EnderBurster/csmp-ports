package aureum.asta.disks.ports.blast.common.init;

import aureum.asta.disks.ports.blast.common.Blast;
import aureum.asta.disks.ports.blast.common.entity.BombEntity;
import aureum.asta.disks.ports.blast.common.item.BombItem;
import aureum.asta.disks.ports.blast.common.item.PipeBombItem;
import aureum.asta.disks.ports.blast.common.item.TriggerBombItem;
import aureum.asta.disks.item.ModItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ProjectileDispenserBehavior;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;

public class BlastItems {
    public static Item BOMB;
    public static Item TRIGGER_BOMB;
    public static Item GOLDEN_BOMB;
    public static Item GOLDEN_TRIGGER_BOMB;
    public static Item DIAMOND_BOMB;
    public static Item DIAMOND_TRIGGER_BOMB;
    public static Item NAVAL_MINE;
    public static Item CONFETTI_BOMB;
    public static Item CONFETTI_TRIGGER_BOMB;
    public static Item DIRT_BOMB;
    public static Item DIRT_TRIGGER_BOMB;
    public static Item PEARL_BOMB;
    public static Item PEARL_TRIGGER_BOMB;
    public static Item SLIME_BOMB;
    public static Item SLIME_TRIGGER_BOMB;
    public static Item AMETHYST_BOMB;
    public static Item AMETHYST_TRIGGER_BOMB;
    public static Item FROST_BOMB;
    public static Item FROST_TRIGGER_BOMB;
    public static Item PIPE_BOMB;

    public static void init() {
        BOMB = registerItem(new BombItem(new Item.Settings().maxCount(16), BlastEntities.BOMB), "bomb", ModItemGroup.BlastGroup);
        TRIGGER_BOMB = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.TRIGGER_BOMB), "trigger_bomb", ModItemGroup.BlastGroup);
        GOLDEN_BOMB = registerItem(new BombItem(new Item.Settings().maxCount(16), BlastEntities.GOLDEN_BOMB), "golden_bomb", ModItemGroup.BlastGroup);
        GOLDEN_TRIGGER_BOMB = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.GOLDEN_TRIGGER_BOMB), "golden_trigger_bomb", ModItemGroup.BlastGroup);
        DIAMOND_BOMB = registerItem(new BombItem(new Item.Settings().maxCount(16), BlastEntities.DIAMOND_BOMB), "diamond_bomb", ModItemGroup.BlastGroup);
        DIAMOND_TRIGGER_BOMB = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.DIAMOND_TRIGGER_BOMB), "diamond_trigger_bomb", ModItemGroup.BlastGroup);
        NAVAL_MINE = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.NAVAL_MINE), "naval_mine", ModItemGroup.BlastGroup);
        CONFETTI_BOMB = registerItem(new BombItem(new Item.Settings().maxCount(16), BlastEntities.CONFETTI_BOMB), "confetti_bomb", ModItemGroup.BlastGroup);
        CONFETTI_TRIGGER_BOMB = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.CONFETTI_TRIGGER_BOMB), "confetti_trigger_bomb", ModItemGroup.BlastGroup);
        DIRT_BOMB = registerItem(new BombItem(new Item.Settings().maxCount(16), BlastEntities.DIRT_BOMB), "dirt_bomb", ModItemGroup.BlastGroup);
        DIRT_TRIGGER_BOMB = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.DIRT_TRIGGER_BOMB), "dirt_trigger_bomb", ModItemGroup.BlastGroup);
        PEARL_BOMB = registerItem(new BombItem(new Item.Settings().maxCount(16), BlastEntities.PEARL_BOMB), "pearl_bomb", ModItemGroup.BlastGroup);
        PEARL_TRIGGER_BOMB = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.PEARL_TRIGGER_BOMB), "pearl_trigger_bomb", ModItemGroup.BlastGroup);
        SLIME_BOMB = registerItem(new BombItem(new Item.Settings().maxCount(16), BlastEntities.SLIME_BOMB), "slime_bomb", ModItemGroup.BlastGroup);
        SLIME_TRIGGER_BOMB = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.SLIME_TRIGGER_BOMB), "slime_trigger_bomb", ModItemGroup.BlastGroup);
        AMETHYST_BOMB = registerItem(new BombItem(new Item.Settings().maxCount(16), BlastEntities.AMETHYST_BOMB), "amethyst_bomb", ModItemGroup.BlastGroup);
        AMETHYST_TRIGGER_BOMB = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.AMETHYST_TRIGGER_BOMB), "amethyst_trigger_bomb", ModItemGroup.BlastGroup);
        FROST_BOMB = registerItem(new BombItem(new Item.Settings().maxCount(16), BlastEntities.FROST_BOMB), "frost_bomb", ModItemGroup.BlastGroup);
        FROST_TRIGGER_BOMB = registerItem(new TriggerBombItem(new Item.Settings().maxCount(16), BlastEntities.FROST_TRIGGER_BOMB), "frost_trigger_bomb", ModItemGroup.BlastGroup);
        PIPE_BOMB = registerItem(new PipeBombItem(new Item.Settings().maxCount(16)), "pipe_bomb", ModItemGroup.BlastGroup);
    }

    public static Item registerItem(Item item, String name,ItemGroup itemGroupKey) {
        if (item instanceof BombItem) {
            registerItem(item, name, itemGroupKey, true);
        } else {
            registerItem(item, name, itemGroupKey, false);
        }
        return item;
    }

    public static Item registerItem(Item item, String name, ItemGroup itemGroupKey, boolean registerDispenserBehavior) {
        Registry.register(Registries.ITEM, Blast.MODID + ":" + name, item);
        ItemGroupEvents.modifyEntriesEvent(itemGroupKey).register((entries) -> entries.add(item));

        if (registerDispenserBehavior) {
            DispenserBlock.registerBehavior(item, new ProjectileDispenserBehavior() {
                @Override
                protected ProjectileEntity createProjectile(World world, Position position, ItemStack itemStack) {
                    BombEntity bombEntity = ((BombItem) itemStack.getItem()).getType().create(world);
                    bombEntity.setPos(position.getX(), position.getY(), position.getZ());
                    itemStack.decrement(1);
                    return bombEntity;
                }
            });
        }

        return item;
    }

}