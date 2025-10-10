package aureum.asta.disks.ports.amarite.mialib.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.CommonTickingComponent;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;
import aureum.asta.disks.ports.amarite.mialib.MiaLib;

public class IdCooldownComponent implements AutoSyncedComponent, CommonTickingComponent {
   private final PlayerEntity player;
   private final Map<Identifier, IdCooldownComponent.Entry> cooldowns = new HashMap<>();
   private int tick;

   public IdCooldownComponent(PlayerEntity player) {
      this.player = player;
   }

   @NotNull
   public static IdCooldownComponent get(PlayerEntity player) {
      return (IdCooldownComponent) MiaLib.ID_COOLDOWN_COMPONENT.get(player);
   }

   public void sync() {
      MiaLib.ID_COOLDOWN_COMPONENT.sync(this.player);
   }

   public void tick() {
      this.tick++;

      for (Map.Entry<Identifier, IdCooldownComponent.Entry> entry : this.cooldowns.entrySet()) {
         if (entry.getValue().endTick < this.tick) {
            this.cooldowns.remove(entry.getKey());
            this.sync();
         }
      }
   }

   public boolean isCoolingDown(Identifier id) {
      return this.cooldowns.containsKey(id);
   }

   public int getCooldown(Identifier id) {
      IdCooldownComponent.Entry entry = this.cooldowns.get(id);
      return entry == null ? 0 : entry.endTick - entry.startTick;
   }

   public float getCooldown(Identifier id, float tickDelta) {
      if (!this.cooldowns.containsKey(id)) {
         return 0.0F;
      } else {
         IdCooldownComponent.Entry entry = this.cooldowns.get(id);
         int f = entry.endTick - entry.startTick;
         float g = (float)(entry.endTick - this.tick) + tickDelta;
         return MathHelper.clamp(g / (float)f, 0.0F, 1.0F);
      }
   }

   public void setCooldown(Identifier id, int ticks) {
      if (ticks <= 0) {
         if (this.cooldowns.remove(id) != null) {
            this.sync();
         }
      } else {
         this.cooldowns.put(id, new IdCooldownComponent.Entry(this.tick, this.tick + ticks));
         this.sync();
      }
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.tick = tag.getInt("tick");
      this.cooldowns.clear();
      NbtCompound compound = tag.getCompound("cooldowns");
      if (compound != null) {
         for (String id : compound.getKeys()) {
            NbtCompound entry = compound.getCompound(id);
            if (entry != null) {
               this.cooldowns.put(new Identifier(id), new IdCooldownComponent.Entry(entry.getInt("start"), entry.getInt("end")));
            }
         }
      }
   }

   public void writeToNbt(NbtCompound tag) {
      tag.putInt("tick", this.tick);
      NbtCompound compound = new NbtCompound();

      for (Identifier id : this.cooldowns.keySet()) {
         NbtCompound entry = new NbtCompound();
         entry.putInt("start", this.cooldowns.get(id).startTick);
         entry.putInt("end", this.cooldowns.get(id).endTick);
         compound.put(id.toString(), entry);
      }

      tag.put("cooldowns", compound);
   }

   static record Entry(int startTick, int endTick) {
   }
}
