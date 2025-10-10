package aureum.asta.disks.ports.amarite.mialib.util;

import aureum.asta.disks.ports.amarite.mialib.MiaLib;

public interface MMath {
   static int lerp(int a, int b, double t) {
      return (int)Math.round((double)a + (double)(b - a) * t);
   }

   static long lerp(long a, long b, double t) {
      return Math.round((double)a + (double)(b - a) * t);
   }

   static float lerp(float a, float b, double t) {
      return (float)((double)a + (double)(b - a) * t);
   }

   static double lerp(double a, double b, double t) {
      return a + (b - a) * t;
   }

   static int clampLoop(int value, int min, int max) {
      int range = max - min + 1;

      while (value < min) {
         value += range;
      }

      while (value > max) {
         value -= range;
      }

      return value;
   }

   static long clampLoop(long value, long min, long max) {
      long range = max - min + 1L;

      while (value < min) {
         value += range;
      }

      while (value > max) {
         value -= range;
      }

      return value;
   }

   static float clampLoop(float value, float min, float max) {
      float range = max - min + 1.0F;

      while (value < min) {
         value += range;
      }

      while (value > max) {
         value -= range;
      }

      return value;
   }

   static double clampLoop(double value, double min, double max) {
      double range = max - min + 1.0;

      while (value < min) {
         value += range;
      }

      while (value > max) {
         value -= range;
      }

      return value;
   }

   static boolean getByteFlag(byte data, int flag) {
      if (flag >= 0 && flag < 8) {
         return (data >> flag & 1) == 1;
      } else {
         MiaLib.LOGGER.warn("Invalid byte flag index: " + flag);
         return false;
      }
   }

   static byte setByteFlag(byte data, int flag, boolean value) {
      if (flag < 0 || flag >= 8) {
         MiaLib.LOGGER.warn("Invalid byte flag index: " + flag);
         return data;
      } else {
         return value ? (byte)(data | 1 << flag) : (byte)(data & ~(1 << flag));
      }
   }

   public static int packRgb(float r, float g, float b) {
      return packRgb(floor(r * 255.0F), floor(g * 255.0F), floor(b * 255.0F));
   }

   public static int packRgb(int r, int g, int b) {
      int i = (r << 8) + g;
      i = (i << 8) + b;
      return i;
   }

   public static int floor(float value) {
      int i = (int)value;
      return value < (float)i ? i - 1 : i;
   }
}
