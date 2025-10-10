package aureum.asta.disks.ports.bundle.client;

import aureum.asta.disks.ports.bundle.BundleBackportish;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.BundleItem;

public class BundleBackportishClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BundleBackportishClientNetworking.register();

		ModelPredicateProviderRegistry.register(BundleBackportish.id("filled"), (stack, world, entity, seed) -> {
			var item = stack.getItem();
			if (!(item instanceof BundleItem)) return Float.NEGATIVE_INFINITY;
			return BundleItem.getAmountFilled(stack);
		});
	}

}
