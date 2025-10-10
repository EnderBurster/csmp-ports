package aureum.asta.disks.client;

import aureum.asta.disks.blocks.client.CreationBlockRenderer;
import aureum.asta.disks.blocks.client.PedestalBlockRenderer;
import aureum.asta.disks.client.particle.AstaParticles;
import aureum.asta.disks.blocks.client.BarrierBlockRenderer;
import aureum.asta.disks.client.render.grimoire.*;
import aureum.asta.disks.init.*;
import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.BackWeaponComponent;
import aureum.asta.disks.client.event.EnchantedToolsHaveEfficiencyEvent;
import aureum.asta.disks.client.render.BloodScytheEntityRenderer;
import aureum.asta.disks.index.ArsenalEntities;
import aureum.asta.disks.index.ArsenalParticles;
import aureum.asta.disks.interfaces.BackslotLarge;
import aureum.asta.disks.item.AstaItems;
import aureum.asta.disks.item.client.GrimoireItemRenderer;
import aureum.asta.disks.item.client.HeldBookAnimation;
import aureum.asta.disks.packets.MeteorPacket;
import aureum.asta.disks.packets.ThunderstruckPacket;
import aureum.asta.disks.packets.UseThunderstruckPacket;
import aureum.asta.disks.ports.enchancement.SyncEnchantingTableBookshelfCountPayload;
import aureum.asta.disks.ports.mason.init.MasonObjects;
import aureum.asta.disks.particle.contract.ColoredParticleInitialData;
import aureum.asta.disks.util.WeaponSlotCallback;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;

import java.util.Locale;

public class AureumAstaDisksClient implements ClientModInitializer {

	public static KeyBinding weaponKeybind;
	public static KeyBinding swapKeybind;
	public static ModelTransformationMode currentMode = ModelTransformationMode.NONE;
	public static final Identifier SPAWN_PARTICLE_PACKET_ID = AureumAstaDisks.id("spawn_particle");

	@Override
	public void onInitializeClient() {

		initPackets();
		initEvents();
		initRenderer();
		initEntities();
		initTickers();
		initBlockRenderLayers();
		initItemModels();
		initBlockEntityRenderer();

		AstaRenderLayers.init();
		ArsenalParticles.registerFactories();
		AstaParticles.registerFactories();

		WeaponSlotCallback.EVENT.register((WeaponSlotCallback)(player, stack) -> stack.getItem() == AstaItems.BLOOD_SCYTHE || stack.getItem() == MasonObjects.GLAIVE || stack.getItem() instanceof BackslotLarge ? ActionResult.FAIL : ActionResult.PASS);
	}

	static {
		for (ModelTransformationMode mode : ModelTransformationMode.values()) {
			ModelPredicateProviderRegistry.register(AureumAstaDisks.id(mode.name().toLowerCase(Locale.ROOT)), (stack, world, entity, seed) -> mode == currentMode ? 1.0F : 0.0F);
		}
	}

	private void initBlockEntityRenderer()
	{
		BlockEntityRendererFactories.register(AstaBlockEntities.BARRIER_BLOCK, BarrierBlockRenderer::new);
		BlockEntityRendererFactories.register(AstaBlockEntities.CREATION_BLOCK, CreationBlockRenderer::new);
		BlockEntityRendererFactories.register(AstaBlockEntities.AMP_BLOCK, PedestalBlockRenderer::new);
	}

	private void initBlockRenderLayers()
	{
		BlockRenderLayerMap.INSTANCE.putBlock(AstaBlocks.KYRATOS_DOOR, RenderLayer.getCutout());
		BlockRenderLayerMap.INSTANCE.putBlock(AstaBlocks.KYRATOS_GLASS, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(AstaBlocks.KYRATOS_GLASS_PANE, RenderLayer.getTranslucent());
		BlockRenderLayerMap.INSTANCE.putBlock(AstaBlocks.AMP_RUNE, RenderLayer.getCutout());
	}

	private void initEntities()
	{
		EntityRendererRegistry.register(ArsenalEntities.BLOOD_SCYTHE, BloodScytheEntityRenderer::new);
		EntityRendererRegistry.register(AstaEntities.GRIMOIRE_PAGE, GrimoirePageEntityRenderer::new);
		EntityRendererRegistry.register(AstaEntities.GRIMOIRE_AQUABLADE, GrimoireAquabladeEntityRenderer::new);
		EntityRendererRegistry.register(AstaEntities.GRIMOIRE_VORTEX_PROJECTILE, GrimoireVortexProjectileEntityRenderer::new);
		EntityRendererRegistry.register(AstaEntities.GRIMOIRE_VORTEX, GrimoireVortexEntityRenderer::new);
		EntityRendererRegistry.register(AstaEntities.GRIMOIRE_SHARK, SharkRenderer::new);
	}

	private void initTickers()
	{
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			HeldBookAnimation.clientTick();
		});

		/*ClientTickEvents.START_WORLD_TICK.register(
				world -> (world.getComponent(AureumAstaDisks.KYRATOS))
						.clientTick(MinecraftClient.getInstance(), world)
		);*/
	}

	private void initItemModels()
	{
		Identifier grimoireId = AureumAstaDisks.id("grimoire");
		GrimoireItemRenderer grimoireItemRenderer = new GrimoireItemRenderer(grimoireId);

		BuiltinItemRendererRegistry.INSTANCE.register(AstaItems.GRIMOIRE, grimoireItemRenderer);
		ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(grimoireItemRenderer);

		ModelLoadingPlugin.register((manager) -> {
			manager.addModels(
					new ModelIdentifier(grimoireId.getNamespace(), grimoireId.getPath() + "_gui", "inventory"),
					new ModelIdentifier(grimoireId.getNamespace(), grimoireId.getPath() + "_handheld", "inventory")
			);
		});
	}

	private void initRenderer()
	{
		WorldRenderEvents.END.register((context) -> {
			context.world().getComponent(AureumAstaDisks.KYRATOS).renderTick(context);
		});
	}

	private void initEvents()
	{
		ItemTooltipCallback.EVENT.register(new EnchantedToolsHaveEfficiencyEvent());
	}

	private void initPackets()
	{
		ClientPlayNetworking.registerGlobalReceiver(
				AureumAstaDisks.CLIENTBOUND_SWEEP_PACKET,
				(client, handler, buf, responseSender) -> {
					int color = buf.readInt();
					int shadowColor = buf.readInt();
					double x = buf.readDouble();
					double y = buf.readDouble();
					double z = buf.readDouble();

					client.execute(
							() -> {
								if (client.world != null)
								{
									client.world.addParticle(ArsenalParticles.SWEEP_PARTICLE.setData(new ColoredParticleInitialData(color)), x, y, z, 0.0, 0.0, 0.0);
									client.world.addParticle(ArsenalParticles.SWEEP_SHADOW_PARTICLE.setData(new ColoredParticleInitialData(shadowColor)), x, y, z, 0.0, 0.0, 0.0);
								}
							}
					);
				}
		);

		weaponKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.arsenal.select_weapon", 82, "category.asta"));
		swapKeybind = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.arsenal.swap_weapon", 71, "category.asta"));
		ClientTickEvents.END_CLIENT_TICK.register((ClientTickEvents.EndTick) client -> {
			if (weaponKeybind.wasPressed() && client.player != null) {
				BackWeaponComponent.setHoldingBackWeapon(client.player, !BackWeaponComponent.isHoldingBackWeapon(client.player));
			}

			if (swapKeybind.wasPressed()) {
				ClientPlayNetworking.send(AureumAstaDisks.SERVERBOUND_SWAP_WEAPON_PACKET, PacketByteBufs.empty());
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SPAWN_PARTICLE_PACKET_ID, (client, handler, buf, responseSender) -> {
			double x = buf.readDouble();
			double y = buf.readDouble();
			double z = buf.readDouble();

			client.execute(() -> {
				if (client.world != null) {
					client.world.addParticle(
							ParticleTypes.DAMAGE_INDICATOR,
							x, y, z,
							0, 0, 0
					);
				}
			});
		});

		SyncEnchantingTableBookshelfCountPayload.registerReceiver();
		UseThunderstruckPacket.receive();
		ThunderstruckPacket.receive();
		MeteorPacket.receive();
	}
}