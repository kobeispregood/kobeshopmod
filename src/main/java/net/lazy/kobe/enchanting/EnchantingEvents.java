package net.lazy.kobe.enchanting;

import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.MasteryXpCentral;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.EnchantmentMenu;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;

public class EnchantingEvents {

    @SubscribeEvent
    public void onXpChange(PlayerXpEvent.LevelChange event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        int delta = event.getLevels();

        // Only XP LOSS
        if (delta >= 0) return;

        // Only enchanting table (prevents death / bottles / commands)
        if (!(player.containerMenu instanceof EnchantmentMenu)) return;

        int levelsSpent = Math.abs(delta);
        int xp = levelsSpent * 20;

        // ✅ CORRECT XP PATH
        MasteryXpCentral.addXp(
                player,
                MasteryType.ENCHANTING,
                xp
        );
    }
}
