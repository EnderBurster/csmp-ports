package aureum.asta.disks;

import aureum.asta.disks.blocks.BarrierBlock;
import aureum.asta.disks.cca.blocks.WaterBarrier;
import aureum.asta.disks.cca.grimoire.GrimoireAquabladeComponent;
import aureum.asta.disks.cca.grimoire.GrimoirePageComponent;
import aureum.asta.disks.cca.grimoire.GrimoireSharksComponent;
import aureum.asta.disks.cca.grimoire.GrimoireVortexComponent;
import aureum.asta.disks.cca.world.KyratosWorldComponent;
import aureum.asta.disks.client.particle.AstaParticles;
import aureum.asta.disks.effect.AstaStatusEffects;
import aureum.asta.disks.entity.grimoire.SharkEntity;
import aureum.asta.disks.events.ChainLightningEvent;
import aureum.asta.disks.events.WindburstEvent;
import aureum.asta.disks.init.AstaBlockEntities;
import aureum.asta.disks.init.AstaEntities;
import aureum.asta.disks.ports.amarite.mialib.MRegistry;
import aureum.asta.disks.cca.BackWeaponComponent;
import aureum.asta.disks.index.*;
import aureum.asta.disks.init.AstaBlocks;
import aureum.asta.disks.init.AstaEnchantments;
import aureum.asta.disks.item.ModItemGroup;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.loot.AstaLootGenerator;
import aureum.asta.disks.ports.charter.common.component.QueuedBlockChange;
import aureum.asta.disks.recipe.RuneCraftingRecipe;
import aureum.asta.disks.recipe.TrimRecipe;
import aureum.asta.disks.sound.AstaSounds;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.fabricmc.fabric.api.registry.FlattenableBlockRegistry;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AureumAstaDisks implements ModInitializer, EntityComponentInitializer, WorldComponentInitializer {
	public static final String MOD_ID = "aureum-asta-disks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final MRegistry REGISTRY = new MRegistry("aureum-asta-disks");
	private static MinecraftServer server;

	public static final Identifier CLIENTBOUND_SWEEP_PACKET = id("sweep");
	public static final Identifier SERVERBOUND_HOLD_WEAPON_PACKET = id("hold_weapon");
	public static final Identifier SERVERBOUND_SWAP_WEAPON_PACKET = id("swap_weapon");
	public static final Identifier SERVERBOUND_SWAP_INVENTORY_PACKET = id("swap_inventory");

	public static final ComponentKey<GrimoirePageComponent> PAGE = ComponentRegistry.getOrCreate(id("page"), GrimoirePageComponent.class);
	public static final ComponentKey<GrimoireAquabladeComponent> AQUABLADE = ComponentRegistry.getOrCreate(id("aquablade"), GrimoireAquabladeComponent.class);
	public static final ComponentKey<GrimoireVortexComponent> VORTEX = ComponentRegistry.getOrCreate(id("vortex"), GrimoireVortexComponent.class);
	public static final ComponentKey<GrimoireSharksComponent> SHARKS = ComponentRegistry.getOrCreate(id("sharks"), GrimoireSharksComponent.class);

	public static final ComponentKey<KyratosWorldComponent> KYRATOS = ComponentRegistry.getOrCreate(id("kyratos"), KyratosWorldComponent.class);

	public static final RecipeSerializer<TrimRecipe> TRIM_RECIPE = Registry.register(Registries.RECIPE_SERIALIZER, AureumAstaDisks.id("trim_recipe_serializer"), new TrimRecipe.Serializer());
	public static final RecipeSerializer<RuneCraftingRecipe> RUNE_RECIPE = Registry.register(Registries.RECIPE_SERIALIZER, AureumAstaDisks.id("rune_recipe"), new RuneCraftingRecipe.Serializer());

	public static Identifier id(String path)
	{
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {

		ServerLifecycleEvents.SERVER_STARTED.register(s -> server = s);

		contentTweaks();

		ModItemGroup.registerItemGroup();
		AstaItems.registerModItems();
		AstaBlocks.init();
		AstaBlockEntities.initialize();
		AstaLootGenerator.init();
		AstaEnchantments.init();
		ArsenalParticles.initialize();
		AstaParticles.initialize();
		AstaEntities.initialize();
		AstaStatusEffects.init();
		ArsenalEntities.initialize();
		ArsenalEnchantments.initialize();
		ArsenalSounds.initialize();
		AstaSounds.init();
		ArsenalStatusEffects.initialize();
		backslotPackets();
		initRecipes();

		ServerLivingEntityEvents.ALLOW_DAMAGE.register(new ChainLightningEvent());
		UseItemCallback.EVENT.register(new WindburstEvent());

		FabricDefaultAttributeRegistry.register(AstaEntities.GRIMOIRE_SHARK, SharkEntity.createAttributes());

		PlayerBlockBreakEvents.AFTER.register((PlayerBlockBreakEvents.After)(world, player, pos, state, blockEntity) -> {
			if (!world.isClient) {
				for (WaterBarrier bar : (world.getComponent(AureumAstaDisks.KYRATOS)).barriers) {
					boolean bl = bar.shouldReverseBlockChange(pos) && !bar.isOwner(player);
					if (bl && !(world.getBlockState(pos).getBlock() instanceof BarrierBlock)) {
						(world.getComponent(AureumAstaDisks.KYRATOS)).addBlockChange(new QueuedBlockChange(200, pos, state));
						break;
					}
				}
			}
		});
	}

	private void initRecipes()
	{
		Registry.register(Registries.RECIPE_TYPE, id(TrimRecipe.Type.ID), TrimRecipe.Type.INSTANCE);
		Registry.register(Registries.RECIPE_TYPE, id(RuneCraftingRecipe.Type.ID), RuneCraftingRecipe.Type.INSTANCE);
	}

	private void backslotPackets()
	{
		ServerPlayNetworking.registerGlobalReceiver(SERVERBOUND_HOLD_WEAPON_PACKET, (server, player, handler, buf, responseSender) -> {
			boolean hold = buf.readBoolean();
			BackWeaponComponent.setHoldingBackWeapon(player, hold);
		});

		ServerPlayNetworking.registerGlobalReceiver(SERVERBOUND_SWAP_WEAPON_PACKET, (server, player, handler, buf, responseSender) -> {
			if (!player.isSpectator()) {
				boolean toggled = BackWeaponComponent.isHoldingBackWeapon(player);
				BackWeaponComponent.setHoldingBackWeapon(player, false);
				ItemStack itemStack = BackWeaponComponent.getBackWeapon(player);
				boolean success = BackWeaponComponent.setBackWeapon(player, player.getStackInHand(Hand.MAIN_HAND));
				if (success) {
					player.setStackInHand(Hand.MAIN_HAND, itemStack);
				}

				player.clearActiveItem();
				BackWeaponComponent.setHoldingBackWeapon(player, toggled);
			}
		});

		ServerPlayNetworking.registerGlobalReceiver(SERVERBOUND_SWAP_INVENTORY_PACKET, (server, player, handler, buf, responseSender) -> {
			int slotId = buf.readInt();
			if (!player.isSpectator()) {
				if (!player.currentScreenHandler.isValid(slotId)) {
					return;
				}

				Slot slot = player.currentScreenHandler.getSlot(slotId);
				ItemStack itemStack = BackWeaponComponent.getBackWeapon(player);
				boolean success = BackWeaponComponent.setBackWeapon(player, slot.getStack());
				if (success) {
					slot.setStack(itemStack);
				}
			}
		});
	}

	private void contentTweaks()
	{
		CompostingChanceRegistry.INSTANCE.add(Items.ROTTEN_FLESH, 0.3F);
		CompostingChanceRegistry.INSTANCE.add(Items.EGG, 0.3F);
		CompostingChanceRegistry.INSTANCE.add(Items.BAMBOO, 0.3F);
		CompostingChanceRegistry.INSTANCE.add(Items.POISONOUS_POTATO, 0.8F);
		FlattenableBlockRegistry.register(Blocks.DIRT_PATH, Blocks.DIRT.getDefaultState());
	}

	public static MinecraftServer getServer() {
		return server;
	}

	@Override
	public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
		registry.beginRegistration(PlayerEntity.class, PAGE).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(GrimoirePageComponent::new);
		registry.beginRegistration(PlayerEntity.class, AQUABLADE).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(GrimoireAquabladeComponent::new);
		registry.beginRegistration(PlayerEntity.class, VORTEX).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(GrimoireVortexComponent::new);
		registry.beginRegistration(PlayerEntity.class, SHARKS).respawnStrategy(RespawnCopyStrategy.INVENTORY).end(GrimoireSharksComponent::new);
	}

	@Override
	public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
		registry.register(KYRATOS, KyratosWorldComponent::new);
	}
}