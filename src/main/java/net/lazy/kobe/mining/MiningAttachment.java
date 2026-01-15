package net.lazy.kobe.mining;

import net.lazy.kobe.KobeMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MiningAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                    KobeMod.MOD_ID
            );

    public static final AttachmentType<MiningData> MINING =
            AttachmentType.serializable(MiningData::new).build();

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register("mining", () -> MINING);
        ATTACHMENTS.register(modEventBus);
    }
}
