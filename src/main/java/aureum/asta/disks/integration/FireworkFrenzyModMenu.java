package aureum.asta.disks.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import aureum.asta.disks.ports.other.FireworkFrenzy;

public class FireworkFrenzyModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> FireworkFrenzyConfig.getScreen(parent, FireworkFrenzy.MOD_ID);
	}
}
