package net.lazy.kobe.farming;

import net.lazy.kobe.mastery.MasteryXp;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "kobe")
public class FarmingEvents {

    private static final int BASE_CROP_XP = 20;

    @SubscribeEvent
    public static void onCropBreak(BlockEvent.BreakEvent event) {

        // -----------------------------
        // VALIDATION
        // -----------------------------
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        BlockState state = event.getState();

        IntegerProperty age = getAgeProperty(state);
        if (age == null) return;

        int current = state.getValue(age);
        int max = age.getPossibleValues().stream().max(Integer::compareTo).orElse(0);

        // -----------------------------
        // ONLY FULLY GROWN CROPS
        // -----------------------------
        if (current < max) return;

        int xp = BASE_CROP_XP;

        if (xp <= 0) return;

        // -----------------------------
        // APPLY XP (SINGLE SOURCE OF TRUTH)
        // -----------------------------
        MasteryXp.addFarmingXp(player, xp);
    }

    private static IntegerProperty getAgeProperty(BlockState state) {
        for (var property : state.getProperties()) {
            if (property instanceof IntegerProperty intProp
                    && intProp.getName().equals("age")) {
                return intProp;
            }
        }
        return null;
    }
}
