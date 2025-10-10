package aureum.asta.disks.item.client;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;

public class HeldBookAnimation {
    public static int ticks;

    public static float pageAngle;
    public static float nextPageAngle;

    public static float pageTurningSpeed;
    public static float nextPageTurningSpeed;

    public static float flipRandom;
    public static float flipTurn;

    private static final Random RANDOM = Random.create();

    public static void clientTick() {
        pageTurningSpeed = nextPageTurningSpeed;
        nextPageTurningSpeed += 0.1F;

        if (nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
            float f = flipRandom;

            do {
                flipRandom = flipRandom + (RANDOM.nextInt(4) - RANDOM.nextInt(4));
            } while (f == flipRandom);
        }

        nextPageTurningSpeed = MathHelper.clamp(nextPageTurningSpeed, 0.0F, 1.0F);
        pageAngle = nextPageAngle;
        float h = (flipRandom - nextPageAngle) * 0.4F;
        h = MathHelper.clamp(h, -0.2F, 0.2F);
        flipTurn = flipTurn + (h - flipTurn) * 0.9F;
        nextPageAngle = nextPageAngle + flipTurn;
    }
}
