package net.lazy.kobe.mastery;

import net.lazy.kobe.KobeMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MasteryAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                    KobeMod.MOD_ID
            );

    public static final AttachmentType<MasteryData> MASTERY =
            AttachmentType.builder(MasteryData::new)
                    .serialize(MasteryData.CODEC)
                    .build();

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register("mastery", () -> MASTERY);
        ATTACHMENTS.register(modEventBus);
    }
}
