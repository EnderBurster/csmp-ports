package aureum.asta.disks.api.lodestone.config;

import eu.midnightdust.lib.config.MidnightConfig;
import eu.midnightdust.lib.config.MidnightConfig.Entry;

public class ClientConfig extends MidnightConfig {
   @Entry
   public static boolean DELAYED_RENDERING = true;
   @Entry(
      min = 0.0,
      max = 5.0
   )
   public static double SCREENSHAKE_INTENSITY = 1.0;
}
