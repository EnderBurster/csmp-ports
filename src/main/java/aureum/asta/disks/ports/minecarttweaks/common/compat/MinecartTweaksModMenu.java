package aureum.asta.disks.ports.minecarttweaks.common.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import aureum.asta.disks.ports.minecarttweaks.MinecartTweaks;

public class MinecartTweaksModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> MinecartTweaksConfig.getScreen(parent, MinecartTweaks.MOD_ID);
	}
}
