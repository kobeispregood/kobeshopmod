package net.lazy.kobe.oregen.data;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.lazy.kobe.KobeMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OreGenTierLoader extends SimpleJsonResourceReloadListener {

    public record WeightedBlock(Block block, int weight) {}

    public static final Map<Integer, List<WeightedBlock>> TIERS = new HashMap<>();

    public OreGenTierLoader() {
        super(new Gson(), "oregen/tiers");
    }

    public static List<Component> getTierTooltip(int tier) {

        List<WeightedBlock> list = TIERS.get(tier);
        if (list == null || list.isEmpty()) {
            return List.of(Component.literal("No data").withStyle(ChatFormatting.RED));
        }

        int totalWeight = list.stream().mapToInt(WeightedBlock::weight).sum();
        List<Component> lines = new java.util.ArrayList<>();

        for (WeightedBlock wb : list) {
            int percent = Math.round((wb.weight() * 100f) / totalWeight);

            ChatFormatting color = getColorForBlock(wb.block());

            MutableComponent line = Component.literal("♦ ")
                    .append(Component.translatable(wb.block().getDescriptionId()))
                    .append(Component.literal(" (" + percent + "%)"));

            line.withStyle(color);

            if (wb.block() == Blocks.ANCIENT_DEBRIS) {
                line.withStyle(ChatFormatting.BOLD);
            }

            lines.add(line);
        }

        return lines;
    }


    private static ChatFormatting getColorForBlock(Block block) {

        if (block == Blocks.STONE || block == Blocks.COBBLESTONE) {
            return ChatFormatting.GRAY;
        }

        if (block == Blocks.COAL_ORE || block == Blocks.COAL_BLOCK) {
            return ChatFormatting.DARK_GRAY;
        }

        if (block == Blocks.IRON_ORE || block == Blocks.RAW_IRON_BLOCK) {
            return ChatFormatting.WHITE;
        }

        if (block == Blocks.GOLD_ORE || block == Blocks.RAW_GOLD_BLOCK) {
            return ChatFormatting.GOLD;
        }

        if (block == Blocks.DIAMOND_ORE || block == Blocks.DIAMOND_BLOCK) {
            return ChatFormatting.AQUA;
        }

        if (block == Blocks.ANCIENT_DEBRIS) {
            return ChatFormatting.GOLD;
        }

        return ChatFormatting.GRAY;
    }

    @Override
    protected void apply(
            Map<ResourceLocation, JsonElement> objects,
            ResourceManager manager,
            ProfilerFiller profiler
    ) {
        TIERS.clear();

        for (JsonElement element : objects.values()) {
            OreGenTier tier = new Gson().fromJson(element, OreGenTier.class);

            List<WeightedBlock> blocks = tier.entries().stream()
                    .map(e -> new WeightedBlock(
                            BuiltInRegistries.BLOCK.get(
                                    ResourceLocation.parse(e.block())
                            ),
                            e.weight()
                    ))
                    .toList();

            TIERS.put(tier.tier(), blocks);
        }

        KobeMod.LOGGER.info("[OreGen] Loaded {} ore generator tiers", TIERS.size());
    }
}
