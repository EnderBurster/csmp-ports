package aureum.asta.disks.ports.amarite.mialib.util;

@FunctionalInterface
public interface QuadFunction<A, B, C, D, E> {
   E apply(A var1, B var2, C var3, D var4);
}
