package net.lazy.kobe.shop;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.lazy.kobe.network.NetworkHelper;

public class ShopCommand {

    public ShopCommand(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("shop")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayer();
                            NetworkHelper.openShop(player);
                            return 1;
                        })
        );

    }
}