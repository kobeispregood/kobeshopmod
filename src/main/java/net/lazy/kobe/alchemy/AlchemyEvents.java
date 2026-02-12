package net.lazy.kobe.alchemy;

import net.lazy.kobe.mastery.MasteryType;
import net.lazy.kobe.mastery.MasteryXpCentral;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

public class AlchemyEvents {

    @SubscribeEvent
    public void onContainerClose(PlayerContainerEvent.Close event) {

        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (!(event.getContainer() instanceof BrewingStandMenu menu)) return;

        for (int i = 0; i < 3; i++) {
            ItemStack stack = menu.getSlot(i).getItem();

            if (!isPotion(stack)) continue;

            // ✅ CORRECT XP PATH
            MasteryXpCentral.addXp(
                    player,
                    MasteryType.ALCHEMY,
                    10
            );

            break; // prevent multi-award
        }
    }

    private static boolean isPotion(ItemStack stack) {
        return stack.is(Items.POTION)
                || stack.is(Items.SPLASH_POTION)
                || stack.is(Items.LINGERING_POTION);
    }
}
