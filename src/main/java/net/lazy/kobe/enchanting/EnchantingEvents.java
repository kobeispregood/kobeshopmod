package net.lazy.kobe.enchanting;

import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.MasteryXpCentral;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;

public class EnchantingEvents {

    @SubscribeEvent
    public void onEnchant(PlayerEnchantItemEvent event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        int enchantCount = event.getEnchantments().size();

        int xp = event.getEnchantments().stream()
                .mapToInt(e -> e.level * 10)
                .sum();

        MasteryXpCentral.addXp(
                player,
                MasteryType.ENCHANTING,
                xp
        );
    }
}