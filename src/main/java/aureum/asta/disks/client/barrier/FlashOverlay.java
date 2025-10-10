package aureum.asta.disks.client.barrier;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class FlashOverlay implements HudRenderCallback {
    private static long flashEnd = 0;
    private static int flashColor = 0xFFFFFF;

    public static void triggerFlash(long durationMs, int color) {
        flashEnd = System.currentTimeMillis() + durationMs;
        flashColor = color;
    }

    @Override
    public void onHudRender(MatrixStack matrices, float tickDelta) {
        long now = System.currentTimeMillis();
        if (now < flashEnd) {
            int alpha = (int)(128 * ((flashEnd - now) / 200.0)); // fade out
            int color = (alpha << 24) | flashColor;

            MinecraftClient client = MinecraftClient.getInstance();
            int width = client.getWindow().getScaledWidth();
            int height = client.getWindow().getScaledHeight();

            fill(matrices, 0, 0, width, height, color);
        }
    }

    private void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        DrawableHelper.fill(matrices, x1, y1, x2, y2, color);
    }
}