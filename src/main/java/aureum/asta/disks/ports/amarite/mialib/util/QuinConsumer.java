package aureum.asta.disks.ports.amarite.mialib.util;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface QuinConsumer<A, B, C, D, E> {
   void accept(A var1, B var2, C var3, D var4, E var5);

   default QuinConsumer<A, B, C, D, E> andThen(@NotNull QuinConsumer<? super A, ? super B, ? super C, ? super D, ? super E> after) {
      return (a, b, c, d, e) -> {
         this.accept(a, b, c, d, e);
         after.accept(a, b, c, d, e);
      };
   }
}
