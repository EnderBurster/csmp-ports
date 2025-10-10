package aureum.asta.disks.ports.elysium.cheirosiphon;

import aureum.asta.disks.ports.elysium.CustomEnchantment;
import aureum.asta.disks.ports.elysium.Elysium;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents.AllowChatMessage;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantment.Rarity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.message.MessageBody;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.HoverEvent.Action;

import java.util.UUID;

public class PyromaniacEnchantment extends Enchantment implements CustomEnchantment {
   public PyromaniacEnchantment() {
      super(Rarity.RARE, EnchantmentTarget.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
      ServerMessageEvents.ALLOW_CHAT_MESSAGE
         .register(
            (AllowChatMessage)(message, sender, params) -> {
               if (EnchantmentHelper.getEquipmentLevel(this, sender) <= 0) {
                  return true;
               } else {
                  String originalText = message.getContent().getString();
                  String mmmpphedText = originalText.replaceAll("\\w+", "mmmpph");
                  Text replacementText = Text.literal(mmmpphedText).styled(s ->
                          s.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(originalText)))
                  );

                  SentMessage replacementMessage = SentMessage.of(SignedMessage.ofUnsigned(sender.getUuid(), replacementText.getString()));

                  /*String text = message.getContent().getString();
                  String mmmpphedText = text.replaceAll("\\w+", "mmmpph");
                  Text replacementComponent = Text.literal(mmmpphedText).styled(s -> s.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, message.getContent())));

                  SentMessage replacementMessage = SentMessage.of(
                     SignedMessage.ofUnsigned(MessageMetadata.of(sender.getUuid()), new DecoratedContents(mmmpphedText, replacementComponent))
                  );*/

                  for (ServerPlayerEntity player : sender.server.getPlayerManager().getPlayerList()) {
                     player.sendChatMessage(replacementMessage, sender.shouldFilterMessagesSentTo(player), params);
                  }

                  return false;
               }
            }
         );
   }

   @Override
   public boolean customCanEnchant(ItemStack stack) {
      return stack.isOf(Elysium.CHEIROSIPHON);
   }

   public boolean isAcceptableItem(ItemStack stack) {
      return this.customCanEnchant(stack);
   }
}
