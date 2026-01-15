package net.lazy.kobe.world;

import net.lazy.kobe.KobeMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class IslandOreGenAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                    KobeMod.MOD_ID
            );

    public static final AttachmentType<Integer> LEVEL =
            AttachmentType.builder(() -> 0).build();

    public static void register(IEventBus bus) {
        ATTACHMENTS.register("island_ore_gen_level", () -> LEVEL);
        ATTACHMENTS.register(bus);
    }
}

