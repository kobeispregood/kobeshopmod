package net.lazy.kobe.shop;

import net.lazy.kobe.econ.MoneyAttachment;
import net.lazy.kobe.econ.MoneyData;
import net.lazy.kobe.oregen.OreGenAttachment;
import net.lazy.kobe.oregen.data.OreGenTierLoader;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.minecraft.world.entity.player.Player;

public class ShopEvents {

    private static final int MAX_LEVEL = 5;

    private static final int[] PRICES = {
            500,     // 0 → 1
            1500,    // 1 → 2
            5000,    // 2 → 3
            15000,   // 3 → 4
            50000    // 4 → 5
    };

    @SubscribeEvent
    public static void onReload(AddReloadListenerEvent event) {
        event.addListener(new ShopData());
        event.addListener(new net.lazy.kobe.oregen.data.OreGenTierLoader());
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();

        new ShopCommand(dispatcher);
        net.lazy.kobe.command.BalanceCommand.register(dispatcher);
        new SpawnerPlaceFix();
        net.lazy.kobe.oregen.OreGenCommands.register(dispatcher);
    }

    public static boolean buyOreGenUpgrade(Player player) {

        MoneyData money = player.getData(MoneyAttachment.MONEY);

        // current upgrade level
        int current = player.getData(OreGenAttachment.LEVEL);

        // max level check
        if (current >= MAX_LEVEL) {
            player.sendSystemMessage(
                    Component.literal("Ore Generator is already max level.")
            );
            return false;
        }

        // price based on current level
        int price = PRICES[Math.min(current, PRICES.length - 1)];

        // money check
        if (money.get() < price) {
            player.sendSystemMessage(
                    Component.literal("Not enough money. Cost: $" + price)
            );
            return false;
        }

        // deduct money
        money.remove(price);

        // apply upgrade
        player.setData(OreGenAttachment.LEVEL, current + 1);

        player.sendSystemMessage(
                Component.literal("Ore Generator upgraded to level " + (current + 1))
        );

        return true;
    }
}