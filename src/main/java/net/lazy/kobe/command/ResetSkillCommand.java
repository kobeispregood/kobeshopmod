package net.lazy.kobe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.lazy.kobe.mastery.MasteryType;

import net.lazy.kobe.mining.MiningAttachment;
import net.lazy.kobe.mining.MiningData;
import net.lazy.kobe.mining.MiningSyncPacket;

import net.lazy.kobe.farming.FarmingAttachment;
import net.lazy.kobe.farming.FarmingData;
import net.lazy.kobe.farming.FarmingSyncPacket;

import net.lazy.kobe.network.NetworkHandler;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ResetSkillCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {

        dispatcher.register(
                Commands.literal("mastery")

                        // =============================================================
                        // RESET
                        // =============================================================
                        .then(Commands.literal("reset")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .executes(ctx -> {

                                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                                            String skillInput = StringArgumentType.getString(ctx, "skill");

                                            // -----------------------------
                                            // NEW MASTERY SYSTEM
                                            // -----------------------------
                                            MasteryType type = MasteryType.fromString(skillInput);
                                            if (type != null && type.hasHandler()) {

                                                type.reset(player);

                                                ctx.getSource().sendSuccess(
                                                        () -> Component.literal(
                                                                "§a" + type.getSerializedName() + " Mastery reset."
                                                        ),
                                                        false
                                                );
                                                return 1;
                                            }

                                            // -----------------------------
                                            // LEGACY SYSTEM
                                            // -----------------------------
                                            switch (skillInput.toLowerCase()) {

                                                case "mining" -> {
                                                    MiningData data = player.getData(MiningAttachment.MINING);
                                                    data.reset();

                                                    NetworkHandler.sendToPlayer(
                                                            new MiningSyncPacket(data.getXp()),
                                                            player
                                                    );

                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("§aMining Mastery reset."),
                                                            false
                                                    );
                                                }

                                                case "farming" -> {
                                                    FarmingData data = player.getData(FarmingAttachment.FARMING);
                                                    data.reset();

                                                    NetworkHandler.sendToPlayer(
                                                            new FarmingSyncPacket(
                                                                    data.getXp(),
                                                                    data.getClaimedLevels()
                                                                            .stream()
                                                                            .mapToInt(Integer::intValue)
                                                                            .toArray()
                                                            ),
                                                            player
                                                    );

                                                    ctx.getSource().sendSuccess(
                                                            () -> Component.literal("§aFarming Mastery reset."),
                                                            false
                                                    );
                                                }

                                                default -> {
                                                    if (type != null) {
                                                        ctx.getSource().sendFailure(
                                                                Component.literal(
                                                                        "§cMastery exists but has no handler yet: " + skillInput
                                                                )
                                                        );
                                                    } else {
                                                        ctx.getSource().sendFailure(
                                                                Component.literal("§cUnknown mastery: " + skillInput)
                                                        );
                                                    }
                                                }
                                            }

                                            return 1;
                                        })
                                )
                        )

                        // =============================================================
                        // SET LEVEL
                        // =============================================================
                        .then(Commands.literal("set")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                                .executes(ctx -> {

                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    String skillInput = StringArgumentType.getString(ctx, "skill");
                                                    int level = IntegerArgumentType.getInteger(ctx, "level");

                                                    // -----------------------------
                                                    // NEW MASTERY SYSTEM
                                                    // -----------------------------
                                                    MasteryType type = MasteryType.fromString(skillInput);
                                                    if (type != null && type.hasHandler()) {

                                                        type.setLevel(player, level);

                                                        ctx.getSource().sendSuccess(
                                                                () -> Component.literal(
                                                                        "§a" + type.getSerializedName()
                                                                                + " Mastery set to level " + level
                                                                ),
                                                                false
                                                        );
                                                        return 1;
                                                    }

                                                    // -----------------------------
                                                    // LEGACY SYSTEM
                                                    // -----------------------------
                                                    switch (skillInput.toLowerCase()) {

                                                        case "mining" -> {
                                                            MiningData data = player.getData(MiningAttachment.MINING);
                                                            data.setLevel(level);

                                                            NetworkHandler.sendToPlayer(
                                                                    new MiningSyncPacket(data.getXp()),
                                                                    player
                                                            );

                                                            ctx.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "§aMining Mastery set to level " + level
                                                                    ),
                                                                    false
                                                            );
                                                        }

                                                        case "farming" -> {
                                                            FarmingData data = player.getData(FarmingAttachment.FARMING);
                                                            data.setLevel(level);

                                                            NetworkHandler.sendToPlayer(
                                                                    new FarmingSyncPacket(
                                                                            data.getXp(),
                                                                            data.getClaimedLevels()
                                                                                    .stream()
                                                                                    .mapToInt(Integer::intValue)
                                                                                    .toArray()
                                                                    ),
                                                                    player
                                                            );

                                                            ctx.getSource().sendSuccess(
                                                                    () -> Component.literal(
                                                                            "§aFarming Mastery set to level " + level
                                                                    ),
                                                                    false
                                                            );
                                                        }

                                                        default -> ctx.getSource().sendFailure(
                                                                Component.literal("§cUnknown mastery: " + skillInput)
                                                        );
                                                    }

                                                    return 1;
                                                })
                                        )
                                )
                        )
        );

        // =============================================================
        // ALIAS: /kobe mastery ...
        // =============================================================
        dispatcher.register(
                Commands.literal("kobe")
                        .then(Commands.literal("mastery")
                                .redirect(dispatcher.getRoot().getChild("mastery")))
        );
    }
}
