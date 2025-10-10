package aureum.asta.disks.api.lodestone.helpers;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NbtCompound;

public class NBTHelper {
   public static NbtCompound filterTag(NbtCompound orig, TagFilter filter) {
      if (filter.filters.isEmpty()) {
         return orig;
      } else {
         NbtCompound copy = orig.copy();
         removeTags(copy, filter);
         return copy;
      }
   }

   public static NbtCompound removeTags(NbtCompound tag, TagFilter filter) {
      NbtCompound newTag = new NbtCompound();

      for (String i : filter.filters) {
         if (!tag.contains(i)) {
            for (String key : tag.getKeys()) {
               if (tag.get(key) instanceof NbtCompound ctag) {
                  removeTags(ctag, filter);
               }
            }
         } else if (filter.isWhitelist) {
            newTag.put(i, newTag);
         } else {
            tag.remove(i);
         }
      }

      if (filter.isWhitelist) {
         tag = newTag;
      }

      return tag;
   }

   public static TagFilter create(String... filters) {
      return new TagFilter(filters);
   }

   public static class TagFilter {
      public final ArrayList<String> filters = new ArrayList<>();
      public boolean isWhitelist;

      public TagFilter(String... filters) {
         this.filters.addAll(List.of(filters));
      }

      public TagFilter setWhitelist() {
         this.isWhitelist = true;
         return this;
      }
   }
}
