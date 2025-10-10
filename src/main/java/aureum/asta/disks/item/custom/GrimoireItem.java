package aureum.asta.disks.item.custom;

import aureum.asta.disks.AureumAstaDisks;
import aureum.asta.disks.cca.grimoire.GrimoireSharksComponent;
import aureum.asta.disks.cca.grimoire.GrimoireVortexComponent;
import aureum.asta.disks.entity.grimoire.AquabladeEntity;
import aureum.asta.disks.entity.grimoire.PageEntity;
import aureum.asta.disks.entity.grimoire.SharkEntity;
import aureum.asta.disks.init.AstaEntities;
import aureum.asta.disks.ports.amarite.amarite.Amarite;
import aureum.asta.disks.ports.amarite.amarite.cca.DiscComponent;
import aureum.asta.disks.ports.amarite.amarite.items.AmariteLongswordItem;
import aureum.asta.disks.ports.amarite.amarite.registry.AmariteEnchantments;
import aureum.asta.disks.ports.amarite.mialib.MiaLib;
import aureum.asta.disks.ports.amarite.mialib.cca.HoldingComponent;
import aureum.asta.disks.sound.AstaSounds;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GrimoireItem extends Item {
    private int ticksSinceLastShoot = 0;
    private int ticksSinceLastShootPrev = 0;

    private static final int ANIM_DURATION = 1000; // ms
    private static final String[] PHASES = {
            "From brooding gulfs are we beheld, by that which bears no name.",
            "Its heralds are the stars it fells, the sky and Earth aflame.",
            "Corporeal laws are unwrit, as suns and love retreat.",
            "To cosmic madness laws submit, though stalwart minds entreat.",
            "In luminous space blackened stars, they gaze, accuse, deny.",
            "Roiling, moaning, this realm of ours, in madness lost shall die.",
            "Carrion hordes trill their profane, accord with eldritch plans.",
            "To cosmic forms from tangent planes, we end as we began."
    };

    public GrimoireModes mode = GrimoireModes.DEFAULT;

    public GrimoireItem(Settings settings) {
        super(settings);
    }

    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        ticksSinceLastShootPrev = ticksSinceLastShoot;
        ticksSinceLastShoot = Math.min(120, ++ticksSinceLastShoot);

        if (selected && entity instanceof ServerPlayerEntity player && player.getMainHandStack() == stack) {
            HoldingComponent component = (HoldingComponent) MiaLib.HOLDING.get(player);
            if (!world.isClient() && component.startedAttacking()) {
                this.grimoireUsage(player, world);
            }
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    public void grimoireUsage(ServerPlayerEntity player, World world)
    {
        world.playSound(null, player.getBlockPos(), AstaSounds.GRIMOIRE_PAGE, SoundCategory.PLAYERS, 0.1f, 1f);
        switch (this.mode) {
            case DEFAULT -> {
                this.mode = GrimoireModes.AQUABLADES;
            }
            case AQUABLADES -> {
                this.mode = GrimoireModes.PULL;
            }
            case PULL -> {
                this.mode = GrimoireModes.SUMMON;
            }
            case SUMMON -> {
                this.mode = GrimoireModes.DEFAULT;
            }
        }
    }

    public int getTicksSinceLastShoot()
    {
        return ticksSinceLastShoot;
    }

    public int getTicksSinceLastShootPrev()
    {
        return ticksSinceLastShootPrev;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ticksSinceLastShoot = 0;
        ticksSinceLastShootPrev = 0;

        getMode(player, player.getMainHandStack()).useAbility();

        return TypedActionResult.success(player.getStackInHand(hand));
    }

    @Environment(EnvType.CLIENT)
    public int mialib$getNameColor(ItemStack stack) {
        return ColorHelper.Argb.getArgb(1, 0, 186, 255);
    }

    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, @Nullable World world, @NotNull List<Text> tooltip, TooltipContext context) {
        if(world != null)
        {
            int duration = 4000;
            long now = System.currentTimeMillis();
            int index = (int)((now / duration) % PHASES.length);
            String target = PHASES[index];

            // When did we enter this phase?
            long phaseStart = (now / duration) * duration;
            long elapsed = now - phaseStart;

            if (elapsed < ANIM_DURATION) {
                // We are in the middle of an animation
                String animText = animateTransition(target, elapsed);
                tooltip.add(Text.translatable(animText).mialib$withItalics(true).mialib$withColor(ColorHelper.Argb.getArgb(1, 0, 213, 255)));
            } else {
                // Normal display
                tooltip.add(Text.literal(target).mialib$withItalics(true).mialib$withColor(ColorHelper.Argb.getArgb(1, 0, 213, 255)));
            }
        }
        GrimoireItem.GrimoireMode grimoireMode = getMode(MinecraftClient.getInstance().player, stack);

        tooltip.add(
                Text.translatable(
                                this.getOrCreateTranslationKey() + ".desc_1", new Object[]{MinecraftClient.getInstance().options.attackKey.getBoundKeyLocalizedText().mialib$withColor(7699656)}
                        )
                        .formatted(Formatting.GRAY)
        );
        tooltip.add(
                Text.translatable(
                                this.getOrCreateTranslationKey() + ".desc_2",
                                new Object[]{
                                        MinecraftClient.getInstance().options.useKey.getBoundKeyLocalizedText().mialib$withColor(7699656),
                                        Text.translatable(grimoireMode.getTranslationKey()).mialib$withColor(grimoireMode.getModeColor())
                                }
                        )
                        .formatted(Formatting.GRAY)
        );
    }

    private String animateTransition(String target, long elapsed) {
        double progress = elapsed / (double)ANIM_DURATION; // 0.0 -> 1.0
        int visibleChars = (int)(target.length() * progress);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < target.length(); i++) {
            if (i < visibleChars) {
                // reveal gradually
                sb.append(target.charAt(i));
            } else {
                // obfuscate unrevealed chars
                sb.append("§k").append("X"); // placeholder obfuscated char
            }
        }
        return sb.toString();
    }

    @Environment(EnvType.CLIENT)
    public void mialib$renderCustomBar(ItemRenderer drawContext, TextRenderer renderer, ItemStack stack, int x, int y, String countLabel, MatrixStack matrices) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            GrimoireItem.GrimoireMode grimoireMode = getMode(player, stack);

            RenderSystem.disableDepthTest();
            float width = grimoireMode.getChargeProgress();
            int k = x + 2;
            int l = y + 13;
            int color = width >= 1.0F ? grimoireMode.getModeColor() : -8355712;

            switch (((GrimoireItem)stack.getItem()).mode)
            {
                case DEFAULT, AQUABLADES -> {
                    DrawableHelper.fill(matrices, k, l, k + 13, l + 2, ColorHelper.Argb.getArgb(255, 0, 0, 0));
                    DrawableHelper.fill(matrices, k, l, k + (int)(Math.max(0.0F, (Math.min(1.0F, width) * 13.0F))), l + 1, color);
                }
                case PULL -> {
                    DrawableHelper.fill(matrices, k, l, k + 13, l + 2, ColorHelper.Argb.getArgb(255, 0, 0, 0));

                    for (int i = 0; i < GrimoireVortexComponent.VORTEX_COUNT; i++)
                    {
                        int vortex = (AureumAstaDisks.VORTEX.get(player)).getVortexDuration(i);
                        int color2 = (float) vortex /3 >= 1.0F ? grimoireMode.getModeColor() : -8355712;
                        DrawableHelper.fill(matrices, k + 1 + i*4, l, k + 1 + i*4 + vortex, l + 1, ColorHelper.Argb.getArgb(255, color2 >> 16 & 0xFF, color2 >> 8 & 0xFF, color2 & 0xFF));
                    }
                }
                case SUMMON -> {
                    DrawableHelper.fill(matrices, k, l - 2, k + 13, l + 2, ColorHelper.Argb.getArgb(255, 0, 0, 0));
                    DrawableHelper.fill(matrices, k + 1, l - 2, k + (int)(Math.min(1.0F, width) * 11.0F) + 1, l - 1, ColorHelper.Argb.getArgb(255, color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF));

                    for (int i = 0; i < GrimoireSharksComponent.SHARK_COUNT; i++)
                    {
                        int shark = (AureumAstaDisks.SHARKS.get(player)).getSharkHealth(i);
                        int color2 = (float) shark /3 >= 1.0F ? grimoireMode.getModeColor() : -8355712;
                        DrawableHelper.fill(matrices, k + 1 + i*4, l, k + 1 + i*4 + shark, l + 1, ColorHelper.Argb.getArgb(255, color2 >> 16 & 0xFF, color2 >> 8 & 0xFF, color2 & 0xFF));
                    }
                }
            }

            RenderSystem.enableDepthTest();
        }
    }

    /*public String getModeTranslationKey(ItemStack stack) {
        switch (this.mode) {
            case DEFAULT -> {
                return this.getTranslationKey() + ".default";
            }
            case AQUABLADES -> {
                return this.getTranslationKey() + ".aquablades";
            }
            case PULL -> {
                return this.getTranslationKey() + ".pull";
            }
            case SUMMON -> {
                return this.getTranslationKey() + ".summon";
            }
        }

        return this.getTranslationKey() + ".default";
    }

    public int getModeColor(ItemStack stack) {
        switch (((GrimoireItem)stack.getItem()).mode) {
            case DEFAULT -> {
                return 7699656;
            }
            case AQUABLADES -> {
                return ColorHelper.Argb.getArgb(1,0,255,228);
            }
            case PULL -> {
                return ColorHelper.Argb.getArgb(1,0,255,83);
            }
            case SUMMON -> {
                return ColorHelper.Argb.getArgb(1,255,0,103);
            }
        }

        return 7699656;
    }*/

    @NotNull
    public static GrimoireItem.GrimoireMode getMode(PlayerEntity user, ItemStack stack) {
        switch (((GrimoireItem)stack.getItem()).mode) {
            case DEFAULT -> {
                return AureumAstaDisks.PAGE.get(user);
            }
            case AQUABLADES -> {
                return AureumAstaDisks.AQUABLADE.get(user);
            }
            case PULL -> {
                return AureumAstaDisks.VORTEX.get(user);
            }
            case SUMMON -> {
                return AureumAstaDisks.SHARKS.get(user);
            }
        }

        return AureumAstaDisks.PAGE.get(user);
    }


    @NotNull
    public static GrimoireItem.GrimoireMode[] getModes(PlayerEntity user) {
        return new GrimoireItem.GrimoireMode[]{
                AureumAstaDisks.PAGE.get(user),
                AureumAstaDisks.AQUABLADE.get(user),
                AureumAstaDisks.VORTEX.get(user),
                AureumAstaDisks.SHARKS.get(user)
        };
    }

    public interface GrimoireMode {
        int OVERCHARGE_COLOR = -996254;
        int UNCHARGED_COLOR = -8355712;

        void useAbility();

        int getModeColor();

        float getChargeProgress();

        String getTranslationKey();
    }
}
