package aureum.asta.disks.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class EnchantingClientUtil {

    public static boolean shouldAddParticles(Entity entity) {
        return MinecraftClient.getInstance().gameRenderer.getCamera().isThirdPerson() || entity != MinecraftClient.getInstance().getCameraEntity();
    }

}
