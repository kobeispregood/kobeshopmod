package net.lazy.kobe.mastery;

import com.mojang.serialization.Codec;
import net.lazy.kobe.mastery.handlers.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum MasteryType implements StringRepresentable {

    // =============================================================
    // CORE SKILLS
    // =============================================================

    MINING("mining", new MiningMasteryHandler()),
    FARMING("farming", null),
    COMBAT("combat", new CombatMasteryHandler()),

    // =============================================================
    // FUTURE / ADVANCED SKILLS
    // =============================================================

    FISHING("fishing", new FishingMasteryHandler()),
    FORAGING("foraging", new ForagingMasteryHandler()),
    ALCHEMY("alchemy",  new AlchemyMasteryHandler()),
    ENCHANTING("enchanting", new EnchantingMasteryHandler()),
    RUNECRAFTING("runecrafting", null),
    HUNTS("hunts", null);

    // =============================================================
    // CODEC (UNCHANGED BEHAVIOR)
    // =============================================================

    public static final Codec<MasteryType> CODEC =
            StringRepresentable.fromEnum(MasteryType::values);

    // =============================================================
    // FIELDS
    // =============================================================

    private final String id;
    private final MasteryHandler handler;

    // =============================================================
    // CONSTRUCTOR
    // =============================================================

    MasteryType(String id, MasteryHandler handler) {
        this.id = id;
        this.handler = handler;
    }

    // =============================================================
    // SERIALIZATION
    // =============================================================

    @Override
    public String getSerializedName() {
        return id;
    }

    // =============================================================
    // HANDLER ACCESS
    // =============================================================

    public boolean hasHandler() {
        return handler != null;
    }

    public void reset(ServerPlayer player) {
        if (handler != null) {
            handler.reset(player);
        }
    }

    public void setLevel(ServerPlayer player, int level) {
        if (handler != null) {
            handler.setLevel(player, level);
        }
    }

    // =============================================================
    // COMMAND / STRING LOOKUP
    // =============================================================

    public static MasteryType fromString(String input) {
        for (MasteryType type : values()) {
            if (type.id.equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null;
    }
}
