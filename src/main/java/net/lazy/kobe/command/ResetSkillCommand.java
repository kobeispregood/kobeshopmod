package net.lazy.kobe.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;

import net.lazy.kobe.mastery.MasteryProgress;
import net.lazy.kobe.mastery.MasteryType;

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

                                            MasteryType type = MasteryType.fromString(skillInput);

                                            if (type != null) {

                                                type.reset(player);

                                                ctx.getSource().sendSuccess(
                                                        () -> Component.literal(
                                                                "§a" + type.getSerializedName() + " mastery reset."
                                                        ),
                                                        false
                                                );
                                                return 1;
                                            }

                                            ctx.getSource().sendFailure(
                                                    Component.literal("§cUnknown mastery: " + skillInput)
                                            );

                                            return 0;
                                        })
                                )
                        )

                        // =============================================================
                        // SET LEVEL
                        // =============================================================
                        .then(Commands.literal("set")
                                .then(Commands.argument("skill", StringArgumentType.word())
                                        .then(Commands.argument("level", IntegerArgumentType.integer(0, MasteryProgress.MAX_LEVELS))
                                                .executes(ctx -> {

                                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                                    String skillInput = StringArgumentType.getString(ctx, "skill");
                                                    int level = IntegerArgumentType.getInteger(ctx, "level");

                                                    MasteryType type = MasteryType.fromString(skillInput);

                                                    if (type != null) {

                                                        type.setLevel(player, level);

                                                        ctx.getSource().sendSuccess(
                                                                () -> Component.literal(
                                                                        "§a" + type.getSerializedName()
                                                                                + " mastery set to level " + level
                                                                ),
                                                                false
                                                        );
                                                        return 1;
                                                    }

                                                    ctx.getSource().sendFailure(
                                                            Component.literal("§cUnknown mastery: " + skillInput)
                                                    );

                                                    return 0;
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