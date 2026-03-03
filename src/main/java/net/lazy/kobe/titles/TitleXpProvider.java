package net.lazy.kobe.titles;

import net.lazy.kobe.mastery.MasteryType;
import net.minecraft.server.level.ServerPlayer;

public final class TitleXpProvider {

    private TitleXpProvider() {}

    public static double getXpMultiplier(ServerPlayer player, MasteryType type) {

        var data = player.getData(TitleAttachment.TITLES);
        if (data == null) return 1.0;

        String selected = data.getSelected();
        if (selected == null) return 1.0;

        switch (selected) {
            case "warrior":
                return type == MasteryType.COMBAT ? 1.10 : 1.0;

            case "harvester":
                return type == MasteryType.FARMING ? 1.10 : 1.0;

            default:
                return 1.0;
        }
    }
}