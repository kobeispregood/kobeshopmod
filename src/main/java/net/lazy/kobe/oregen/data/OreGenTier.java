package net.lazy.kobe.oregen.data;

import java.util.List;

public record OreGenTier(
        int tier,
        List<Entry> entries
) {
    public record Entry(String block, int weight) {}
}
