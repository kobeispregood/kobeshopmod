package net.lazy.kobe.oregen;

import com.mojang.serialization.Codec;
import net.lazy.kobe.KobeMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class OreGenAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                    KobeMod.MOD_ID
            );

    /**
     * Ore generator upgrade level
     * DEFAULT: 0
     * PERSISTENT: YES
     */
    public static final AttachmentType<Integer> LEVEL =
            AttachmentType.builder(() -> 0)
                    .serialize(Codec.INT) // <-- REQUIRED FOR SAVE/LOAD
                    .build();

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register("ore_gen_level", () -> LEVEL);
        ATTACHMENTS.register(modEventBus);
    }
}
