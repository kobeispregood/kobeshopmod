package net.lazy.kobe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.lazy.kobe.econ.MoneyAPI;

public class PayCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("pay")
                        .then(
                                Commands.argument("player", EntityArgument.player())
                                        .then(
                                                Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> {

                                                            ServerPlayer sender = ctx.getSource().getPlayer();
                                                            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");

                                                            if (sender == null) return 0;
                                                            if (sender.getUUID().equals(target.getUUID())) {
                                                                sender.sendSystemMessage(Component.literal("§cYou cannot pay yourself."));
                                                                return 0;
                                                            }

                                                            if (!MoneyAPI.tryRemove(sender, amount)) {
                                                                sender.sendSystemMessage(Component.literal("§cInsufficient funds."));
                                                                return 0;
                                                            }

                                                            MoneyAPI.add(target, amount);

                                                            sender.sendSystemMessage(Component.literal("§aYou paid §e" + target.getName().getString() + " §a$" + amount));
                                                            target.sendSystemMessage(Component.literal("§e" + sender.getName().getString() + " §apaid you §a$" + amount));

                                                            return 1;
                                                        })
                                        )
                        )
        );
    }
}
