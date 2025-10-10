package aureum.asta.disks.ports.amarite.mialib.util;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface TriConsumer<A, B, C> {
   void accept(A var1, B var2, C var3);

   default TriConsumer<A, B, C> andThen(@NotNull TriConsumer<? super A, ? super B, ? super C> after) {
      return (a, b, c) -> {
         this.accept(a, b, c);
         after.accept(a, b, c);
      };
   }
}
