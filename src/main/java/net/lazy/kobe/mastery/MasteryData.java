package net.lazy.kobe.mastery;

import com.mojang.serialization.Codec;

import java.util.EnumMap;
import java.util.Map;

public class MasteryData {

    // Persisted map of progress per mastery type
    private final EnumMap<MasteryType, MasteryProgress> progress =
            new EnumMap<>(MasteryType.class);

    // Codec for attachment serialization
    public static final Codec<MasteryData> CODEC =
            Codec.unboundedMap(MasteryType.CODEC, MasteryProgress.CODEC)
                    .xmap(
                            // decode map -> MasteryData
                            map -> {
                                MasteryData data = new MasteryData();
                                for (var e : map.entrySet()) {
                                    data.progress.put(e.getKey(), e.getValue());
                                }
                                return data;
                            },
                            // encode MasteryData -> map
                            data -> data.progress
                    );

    public MasteryProgress getOrCreate(MasteryType type) {
        return progress.computeIfAbsent(type, t -> new MasteryProgress());
    }

    public Map<MasteryType, MasteryProgress> getAll() {
        return progress;
    }

    public void reset(MasteryType type) {
        progress.remove(type);
    }

    public void resetAll() {
        progress.clear();
    }

    public void copyFrom(MasteryData other) {
        this.progress.clear();
        this.progress.putAll(other.progress);
    }
}
