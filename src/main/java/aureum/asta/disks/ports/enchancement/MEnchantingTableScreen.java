package aureum.asta.disks.ports.enchancement;

public interface MEnchantingTableScreen {

    default int getBookshelfCount(){return 0;}

    default void setBookshelfCount(int bookshelfCount){}
}
