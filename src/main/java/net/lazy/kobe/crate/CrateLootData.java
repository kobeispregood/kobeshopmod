package net.lazy.kobe.crate;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.lang.reflect.Type;
import java.util.*;

public class CrateLootData extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();
    private static final Type LIST_TYPE = new TypeToken<List<CrateLootEntry>>() {}.getType();
    private static final Map<CrateType, List<CrateLootEntry>> LOOT = new HashMap<>();
    private static final Random RNG = new Random();

    public CrateLootData() {
        super(GSON, "crate");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> map,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {
        LOOT.clear();

        for (CrateType type : CrateType.values()) {
            JsonElement json = map.get(type.lootFile);
            if (json == null) {
                LOOT.put(type, List.of());
                continue;
            }

            List<CrateLootEntry> entries = GSON.fromJson(json, LIST_TYPE);
            LOOT.put(type, entries);
        }
    }

    /* =========================================================
       ACCESS
       ========================================================= */

    public static List<CrateLootEntry> getLoot(CrateType type) {
        return LOOT.getOrDefault(type, List.of());
    }

    /* =========================================================
       WEIGHTED ROLL
       ========================================================= */

    public static CrateLootEntry rollWeighted(CrateType type) {
        List<CrateLootEntry> loot = getLoot(type);
        if (loot.isEmpty()) {
            return new CrateLootEntry("minecraft:air", 1, 0);
        }

        int totalWeight = loot.stream().mapToInt(CrateLootEntry::weight).sum();
        int roll = RNG.nextInt(totalWeight);

        int cumulative = 0;
        for (CrateLootEntry entry : loot) {
            cumulative += entry.weight();
            if (roll < cumulative) {
                return entry;
            }
        }

        return loot.get(0);
    }

    /* =========================================================
       FINAL REWARD (THIS IS WHAT WAS MISSING)
       ========================================================= */

    public static CrateReward roll(CrateType type) {
        CrateLootEntry entry = rollWeighted(type);
        return entry.toReward();
    }
}
