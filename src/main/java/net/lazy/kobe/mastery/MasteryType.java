package net.lazy.kobe.mastery;

import com.mojang.serialization.Codec;
import net.lazy.kobe.mastery.handlers.DefaultMasteryHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;

public enum MasteryType implements StringRepresentable {

    // =============================================================
    // CORE SKILLS
    // =============================================================

    MINING("mining"),
    FARMING("farming"),
    COMBAT("combat"),

    // =============================================================
    // FUTURE / ADVANCED SKILLS
    // =============================================================

    FISHING("fishing"),
    FORAGING("foraging"),
    ALCHEMY("alchemy"),
    ENCHANTING("enchanting"),
    RUNECRAFTING("runecrafting"),
    HUNTS("hunts");

    // =============================================================
    // CODEC
    // =============================================================

    public static final Codec<MasteryType> CODEC =
            StringRepresentable.fromEnum(MasteryType::values);

    // =============================================================
    // FIELDS
    // =============================================================

    private final String id;
    private final DefaultMasteryHandler handler;

    // =============================================================
    // CONSTRUCTOR
    // =============================================================

    MasteryType(String id) {
        this.id = id;
        this.handler = new DefaultMasteryHandler(this);
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

    public void reset(ServerPlayer player) {
        handler.reset(player);
    }

    public void setLevel(ServerPlayer player, int level) {
        handler.setLevel(player, level);
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