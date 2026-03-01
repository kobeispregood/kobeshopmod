package net.lazy.kobe.combat;

import net.lazy.kobe.mastery.MasteryAttachment;
import net.lazy.kobe.mastery.MasteryData;
import net.lazy.kobe.mastery.MasteryProgress;
import net.lazy.kobe.mastery.MasteryType;
import net.minecraft.server.level.ServerPlayer;

public final class CombatLevelHelper {

    private CombatLevelHelper() {}

    public static int getCombatLevel(ServerPlayer player) {

        MasteryData data = player.getData(MasteryAttachment.MASTERY);

        if (data == null) {
            return 0;
        }

        MasteryProgress progress =
                data.getOrCreate(MasteryType.COMBAT);

        return progress.getLevel();
    }
}