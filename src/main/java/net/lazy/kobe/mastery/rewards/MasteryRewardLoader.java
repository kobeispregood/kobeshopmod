package net.lazy.kobe.mastery.rewards;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Map;

public class MasteryRewardLoader extends SimpleJsonResourceReloadListener {

    private static final Gson GSON = new Gson();

    public MasteryRewardLoader() {
        // Loads all json files under: data/<namespace>/mastery/*.json
        super(GSON, "mastery");
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> jsons,
            ResourceManager resourceManager,
            ProfilerFiller profiler
    ) {
        MasteryRewardRegistry.clear();

        for (var entry : jsons.entrySet()) {
            ResourceLocation fileId = entry.getKey();   // ex: kobe:fishing
            JsonElement json = entry.getValue();

            // fileId.getPath() is "fishing" (because folder is "mastery")
            // Store exactly under that mastery id
            MasteryRewardRegistry.load(
                    ResourceLocation.fromNamespaceAndPath(fileId.getNamespace(), fileId.getPath()),
                    json
            );
        }

        // optional debug:
        // System.out.println("[MasteryRewardLoader] loaded mastery reward sets: " + MasteryRewardRegistry.size());
    }
}
