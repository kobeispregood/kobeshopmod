package net.lazy.kobe.farming;

import net.lazy.kobe.KobeMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class FarmingAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                    KobeMod.MOD_ID
            );

    public static final AttachmentType<FarmingData> FARMING =
            AttachmentType.serializable(FarmingData::new).build();

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register("farming", () -> FARMING);
        ATTACHMENTS.register(modEventBus);
    }
}
