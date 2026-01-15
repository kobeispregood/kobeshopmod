package net.lazy.kobe.farming;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;

@EventBusSubscriber(modid = "kobe")
public class FarmingEvents {

    @SubscribeEvent
    public static void onCropBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        BlockState state = event.getState();

        IntegerProperty age = getAgeProperty(state);
        if (age == null) return;

        int current = state.getValue(age);
        int max = age.getPossibleValues().stream().max(Integer::compareTo).orElse(0);

        // only fully grown crops
        if (current < max) return;

        // 🌾 Farming XP (tuned so leveling actually happens)
        FarmingLeveling.addXp(player, 20);
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
