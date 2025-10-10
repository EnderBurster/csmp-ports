package aureum.asta.disks.mixin.ports.tweaks;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({VillagerEntity.class})
public abstract class VillagerEntityMixin extends MerchantEntity {
   public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
      super(entityType, world);
   }

   @Shadow
   public abstract VillagerData getVillagerData();

   @Inject(
      method = {"clearDailyRestockCount"},
      at = {@At("TAIL")}
   )
   public void clearDailyRestockCount(CallbackInfo callbackInfo) {
      if (!this.hasCustomer()) {
         this.offers = new TradeOfferList();
         this.refillAllRecipes();
      }
   }

   @Unique
   protected void refillAllRecipes() {
      VillagerData villagerData = this.getVillagerData();
      Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap = (Int2ObjectMap<TradeOffers.Factory[]>) TradeOffers.PROFESSION_TO_LEVELED_TRADE.get(villagerData.getProfession());
      if (int2ObjectMap != null && !int2ObjectMap.isEmpty()) {
         for (int i = 1; i <= villagerData.getLevel(); i++) {
            TradeOffers.Factory[] factories = (TradeOffers.Factory[])int2ObjectMap.get(i);
            if (factories == null) {
               return;
            }

            TradeOfferList tradeOfferList = this.getOffers();
            this.fillRecipesFromPool(tradeOfferList, factories, 2);
         }
      }
   }
}
