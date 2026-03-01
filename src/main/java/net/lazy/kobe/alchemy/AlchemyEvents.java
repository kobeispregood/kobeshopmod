package net.lazy.kobe.alchemy;

import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.MasteryXpCentral;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@EventBusSubscriber(modid = "kobe")
public class AlchemyEvents {

    @SubscribeEvent
    public static void onPotionBrew(PlayerBrewedPotionEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        ItemStack stack = event.getStack();
        int level = MasteryXpCentral.getLevel(player, MasteryType.ALCHEMY);

        var contents = stack.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return;

        // ------------------------------------------------
        // GATE LEVEL II POTIONS
        // ------------------------------------------------
        for (MobEffectInstance effect : contents.getAllEffects()) {

            if (effect.getAmplifier() == 1 && level < 15) {

                replaceWithAwkward(player, stack);
                refundIngredient(player);
                playFailureSound(player);

                player.sendSystemMessage(
                        Component.literal("Alchemy 15 required to brew Level II potions!")
                                .withStyle(ChatFormatting.RED)
                );

                return;
            }
        }

        // ------------------------------------------------
        // DURATION SCALING
        // ------------------------------------------------
        if (level > 10) {

            int bonusTicks = (level - 10) * 5 * 20;
            List<MobEffectInstance> newEffects = new ArrayList<>();

            for (MobEffectInstance effect : contents.getAllEffects()) {

                if (!effect.getEffect().value().isBeneficial()) {
                    newEffects.add(effect);
                    continue;
                }

                newEffects.add(new MobEffectInstance(
                        effect.getEffect(),
                        effect.getDuration() + bonusTicks,
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.isVisible(),
                        effect.showIcon()
                ));
            }

            stack.set(
                    DataComponents.POTION_CONTENTS,
                    new PotionContents(
                            contents.potion(),
                            contents.customColor(),
                            newEffects
                    )
            );
        }

        int xp = calculatePotionXp(stack);
        MasteryXpCentral.addXp(player, MasteryType.ALCHEMY, xp);
    }

    // ====================================================
    // Refund Ingredient
    // ====================================================

    private static void refundIngredient(ServerPlayer player) {

        if (!(player.containerMenu instanceof net.minecraft.world.inventory.BrewingStandMenu menu))
            return;

        // Slot 3 = ingredient slot
        ItemStack ingredient = menu.getSlot(3).getItem();

        if (!ingredient.isEmpty()) {
            ingredient.grow(1);
        }
    }

    // ====================================================
    // Failure Sound
    // ====================================================

    private static void playFailureSound(ServerPlayer player) {

        player.level().playSound(
                null,
                player.blockPosition(),
                SoundEvents.BREWING_STAND_BREW,
                SoundSource.BLOCKS,
                0.5f,
                0.5f // lower pitch = fail vibe
        );
    }

    // ====================================================
    // Replace with Awkward
    // ====================================================

    private static void replaceWithAwkward(ServerPlayer player, ItemStack stack) {

        var registry = player.level()
                .registryAccess()
                .registryOrThrow(Registries.POTION);

        ResourceKey<Potion> awkwardKey = ResourceKey.create(
                Registries.POTION,
                ResourceLocation.fromNamespaceAndPath("minecraft", "awkward")
        );

        var awkwardHolder = registry.getHolderOrThrow(awkwardKey);

        stack.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(
                        Optional.of(awkwardHolder),
                        Optional.empty(),
                        List.of()
                )
        );
    }

    private static int calculatePotionXp(ItemStack stack) {

        int xp = 10;

        if (stack.is(Items.SPLASH_POTION)) xp += 5;
        if (stack.is(Items.LINGERING_POTION)) xp += 10;

        var contents = stack.get(DataComponents.POTION_CONTENTS);

        if (contents != null) {
            for (MobEffectInstance effect : contents.getAllEffects()) {
                xp += effect.getAmplifier() * 10;
            }
        }

        return xp;
    }
}