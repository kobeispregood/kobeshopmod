package net.lazy.kobe.titles;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;

import static net.lazy.kobe.KobeMod.MOD_ID;

public final class TitlesAttachment {

    private TitlesAttachment() {}

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<TitleAttachment>> TITLES =
            ATTACHMENTS.register("titles", () -> TitleAttachment.TITLES);

    public static void register(IEventBus bus) {
        ATTACHMENTS.register(bus);
    }
}