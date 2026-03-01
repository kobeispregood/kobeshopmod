package net.lazy.kobe.titles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.HashSet;
import java.util.Set;

public class TitleAttachment {

    // =============================================================
    // DATA
    // =============================================================
    private final Set<String> unlocked;
    private String selected;

    // =============================================================
    // CONSTRUCTORS
    // =============================================================
    public TitleAttachment() {
        this.unlocked = new HashSet<>();
        this.selected = null;
    }

    public TitleAttachment(Set<String> unlocked, String selected) {
        this.unlocked = new HashSet<>(unlocked);
        this.selected = selected;
    }

    // =============================================================
    // CODEC (THIS IS THE IMPORTANT PART)
    // =============================================================
    public static final Codec<TitleAttachment> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.listOf()
                            .fieldOf("unlocked")
                            .forGetter(t -> t.unlocked.stream().toList()),
                    Codec.STRING.optionalFieldOf("selected", "")
                            .forGetter(t -> t.selected == null ? "" : t.selected)
            ).apply(instance, (list, selected) ->
                    new TitleAttachment(new HashSet<>(list),
                            selected.isEmpty() ? null : selected)
            ));

    // =============================================================
    // ATTACHMENT TYPE
    // =============================================================
    public static final AttachmentType<TitleAttachment> TITLES =
            AttachmentType.builder(TitleAttachment::new)
                    .serialize(CODEC)
                    .copyOnDeath()
                    .build();

    // =============================================================
    // LOGIC
    // =============================================================
    public void unlock(String id) {
        unlocked.add(id);
    }

    public boolean has(String id) {
        return unlocked.contains(id);
    }

    public void select(String id) {
        if (unlocked.contains(id)) {
            selected = id;
        }
    }

    public String getSelected() {
        return selected;
    }

    public Set<String> getUnlocked() {
        return unlocked;
    }
}