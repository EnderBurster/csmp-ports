package aureum.asta.disks.ports.amarite.amarite.cca;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.entities.DiscEntity;

public class DiscComponent implements AutoSyncedComponent, ServerTickingComponent {
   public static final int DISC_COUNT = 3;
   public static final int MAX_DISC_DURABILITY = 3;
   public static final int DISC_REPAIR_COOLDOWN = 10;
   public static final int PYLON_COOLDOWN = 40;
   public static final int REBOUND_MAX_CHARGE = 3;
   public static final int ORBIT_MAX_CHARGE = 24;
   public static final int ORBIT_MAX_DURATION = 160;
   private final PlayerEntity player;
   public final List<Integer> discIds = new ArrayList<>();
   public final List<Integer> discDurability = new ArrayList<>();
   private int repairCooldown = 0;
   public int reboundCharge = 3;
   public float orbitCharge = ORBIT_MAX_CHARGE;
   public int orbitDuration = 0;

   public DiscComponent(PlayerEntity playerEntity) {
      this.player = playerEntity;

      for (int i = 0; i < DISC_COUNT; i++) {
         this.discIds.add(-1);
         this.discDurability.add(MAX_DISC_DURABILITY);
      }
   }

   public void sync() {
      Amarite.DISC.sync(this.player);
   }

   /*public void tick() {
      System.out.print(this.orbitDuration);
      if (this.orbitDuration > 0) {
         this.orbitDuration--;
         System.out.print(this.orbitDuration);
         this.orbitCharge = 24.0F * (this.orbitDuration / 160.0F);
         this.sync();
         if (this.orbitDuration <= 0) {
            this.orbitCharge = 0.0F;
            this.sync();
         }
      }
   }*/

   public void serverTick() {
      if (this.repairCooldown > 0) {
         this.repairCooldown--;
         if (this.repairCooldown <= 0) {
            int selected = -1;

            for (int i = 0; i < this.discDurability.size(); i++) {
               if (this.discDurability.get(i) < 3 && (selected == -1 || this.discDurability.get(i) < this.discDurability.get(selected))) {
                  selected = i;
               }
            }

            if (selected != -1) {
               this.discDurability.set(selected, this.discDurability.get(selected) + 1);
               if(this.player.world.getEntityById(this.getDiscId(selected)) instanceof DiscEntity disc)
               {
                  disc.durability = this.discDurability.get(selected);
               }
               this.sync();
            }

            this.repairCooldown = DISC_REPAIR_COOLDOWN;
         }
      }
      if (this.orbitDuration > 0) {
         this.orbitDuration--;
         this.orbitCharge = ORBIT_MAX_CHARGE * (this.orbitDuration / 160.0F);
         this.sync();
         if (this.orbitDuration <= 0) {
            this.orbitCharge = 0.0F;
            this.sync();
         }
      }
   }

   public int getNextAvailableDisc() {
      int selected = -1;

      for (int i = 0; i < (this.player.isCreative() ? 3 : 1) * 3; i++) {
         if (this.discIds.size() <= i) {
            this.discIds.add(-1);
            this.discDurability.add(3);
         }

         Integer id = this.discIds.get(i);
         if (id != -1 && !(this.player.world.getEntityById(id) instanceof DiscEntity)) {
            this.discIds.set(i, -1);
            id = -1;
            this.sync();
         }

         if (id == -1
            && (this.discDurability.get(i) > 0 || this.player.isCreative())
            && (selected == -1 || this.discDurability.get(i) > this.discDurability.get(selected))) {
            selected = i;
         }
      }

      return selected;
   }

   public boolean damage(int index, int damage) {
      int durability = Math.max(0, this.discDurability.get(index) - damage);
      this.discDurability.set(index, durability);
      if (this.repairCooldown <= 0) {
         this.repairCooldown = DISC_REPAIR_COOLDOWN;
      }

      this.sync();
      return durability == 0;
   }

   public List<DiscEntity> getDiscEntities() {
      ArrayList<DiscEntity> discEntities = new ArrayList<>();

      for (int i = 0; i < this.discIds.size(); i++) {
         DiscEntity discEntity = this.getDiscEntity(i);
         if (discEntity != null) {
            discEntities.add(discEntity);
         }
      }

      return discEntities;
   }

   @Nullable
   public DiscEntity getDiscEntity(int index) {
      if (index >= 0 && index < this.discIds.size()) {
         Entity entity = this.player.world.getEntityById(this.getDiscId(index));
         return entity instanceof DiscEntity ? (DiscEntity)entity : null;
      } else {
         return null;
      }
   }

   public void setDiscEntity(int index, @Nullable DiscEntity discEntity) {
      if (index >= 0) {
         if (index >= this.discIds.size()) {
            for (int i = this.discIds.size(); i <= index; i++) {
               this.discIds.add(-1);
            }
         }

         this.setDiscId(index, discEntity == null ? 0 : discEntity.getId());
      }
   }

   public Integer getDiscId(int index) {
      return index >= 0 && index < this.discIds.size() ? this.discIds.get(index) : -1;
   }

   public void setDiscId(int index, int discId) {
      if (index >= 0) {
         if (index >= this.discIds.size()) {
            for (int i = this.discIds.size(); i <= index; i++) {
               this.discIds.add(-1);
            }
         }

         this.discIds.set(index, discId);
         this.sync();
      }
   }

   public Integer getDiscIndex(DiscEntity discEntity) {
      for (int i = 0; i < this.discIds.size(); i++) {
         if (this.discIds.get(i) == discEntity.getId()) {
            return i;
         }
      }

      return -1;
   }

   public int getDiscDurability(int index) {
      return index >= 0 && index < this.discDurability.size() ? this.discDurability.get(index) : -1;
   }

   public void setDiscDurability(int index, int durability) {
      if (index >= 0) {
         if (index >= this.discDurability.size()) {
            for (int i = this.discDurability.size(); i <= index; i++) {
               this.discDurability.add(3);
            }
         }

         this.discDurability.set(index, this.player.isCreative() ? 3 : durability);
         this.sync();
      }
   }

   public void chargeRebound(int amount) {
      this.reboundCharge = Math.min(3, this.reboundCharge + amount);
      this.sync();
   }

   public void chargeOrbit(float amount) {
      this.orbitCharge = Math.min(ORBIT_MAX_CHARGE, this.orbitCharge + amount);
      this.sync();
   }

   public void readFromNbt(@NotNull NbtCompound tag) {
      this.discIds.clear();
      NbtList ids = tag.getList("DiscIds", 3);

      for (int i = 0; i < ids.size(); i++) {
         this.discIds.add(ids.getInt(i));
      }

      this.discDurability.clear();
      NbtList durability = tag.getList("DiscDurability", 3);

      for (int i = 0; i < durability.size(); i++) {
         this.discDurability.add(durability.getInt(i));
      }

      this.reboundCharge = tag.getInt("ReboundCharge");
      this.orbitCharge = tag.getFloat("OrbitCharge");
      this.orbitDuration = tag.getInt("OrbitDuration");
   }

   public void writeToNbt(@NotNull NbtCompound tag) {
      NbtList ids = new NbtList();

      for (Integer discId : this.discIds) {
         ids.add(NbtInt.of(discId));
      }

      tag.put("DiscIds", ids);
      NbtList durability = new NbtList();

      for (Integer integer : this.discDurability) {
         durability.add(NbtInt.of(integer));
      }

      tag.put("DiscDurability", durability);
      tag.putInt("ReboundCharge", this.reboundCharge);
      tag.putFloat("OrbitCharge", this.orbitCharge);
      tag.putInt("OrbitDuration", this.orbitDuration);
   }
}
