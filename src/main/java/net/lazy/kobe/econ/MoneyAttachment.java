package net.lazy.kobe.econ;

import net.lazy.kobe.KobeMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class MoneyAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, KobeMod.MOD_ID);

    // FINAL correct way to attach MoneyData to players in NeoForge 1.21
    public static final AttachmentType<MoneyData> MONEY =
            AttachmentType.serializable(MoneyData::new).build();

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register("money", () -> MONEY);
        ATTACHMENTS.register(modEventBus);
    }
}