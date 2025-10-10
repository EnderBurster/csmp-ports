package aureum.asta.disks.ports.amarite.mialib.interfaces;

import net.minecraft.util.Identifier;

public interface MPlayerEntity {
   default boolean mialib$isCoolingDown(Identifier id) {
      return false;
   }

   default void mialib$setCooldown(Identifier id, int ticks) {
   }

   default int mialib$getCooldown(Identifier id) {
      return 0;
   }

   default float mialib$getCooldown(Identifier id, float tickDelta) {
      return 0.0F;
   }

   default boolean mialib$isAttacking() {
      return false;
   }

   default boolean mialib$startedAttacking() {
      return false;
   }

   default boolean mialib$hasBeenAttacking() {
      return false;
   }

   default boolean mialib$stoppedAttacking() {
      return false;
   }

   default int mialib$getAttackingTime() {
      return 0;
   }

   default boolean mialib$isUsing() {
      return false;
   }

   default boolean mialib$startedUsing() {
      return false;
   }

   default boolean mialib$hasBeenUsing() {
      return false;
   }

   default boolean mialib$stoppedUsing() {
      return false;
   }

   default int mialib$getUsingTime() {
      return 0;
   }
}
