package net.lazy.kobe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.lazy.kobe.econ.EconomyStorage;

import java.util.*;

public class BaltopCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("baltop")
                        .executes(ctx -> show(ctx.getSource(), 1))
                        .then(
                                Commands.argument("page", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            int page = IntegerArgumentType.getInteger(ctx, "page");
                                            return show(ctx.getSource(), page);
                                        })
                        )
        );
    }

    private static int show(CommandSourceStack src, int page) {

        Map<UUID, Integer> map = EconomyStorage.all();
        List<Map.Entry<UUID, Integer>> sorted = new ArrayList<>(map.entrySet());

        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        int perPage = 10;
        int maxPage = Math.max(1, (sorted.size() + perPage - 1) / perPage);

        page = Math.min(Math.max(page, 1), maxPage);

        src.sendSystemMessage(Component.literal("§6§lTop Balances (Page " + page + "/" + maxPage + "):"));

        int start = (page - 1) * perPage;

        for (int i = start; i < Math.min(start + perPage, sorted.size()); i++) {
            var e = sorted.get(i);
            src.sendSystemMessage(Component.literal(
                    "§e#" + (i + 1) + " §7" + e.getKey() + " §f- §a$" + e.getValue()
            ));
        }

        return 1;
    }
}
