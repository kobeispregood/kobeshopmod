package net.lazy.kobe.combat;

import net.lazy.kobe.KobeMod;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class CombatAttachment {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
                    KobeMod.MOD_ID
            );

    public static final AttachmentType<CombatData> COMBAT =
            AttachmentType.builder(CombatData::new)
                    .serialize(CombatData.CODEC)
                    .build();

    public static void register(IEventBus modEventBus) {
        ATTACHMENTS.register("combat", () -> COMBAT);
        ATTACHMENTS.register(modEventBus);
    }

    public static CombatData get(ServerPlayer player) {
        return player.getData(COMBAT);
    }

    public static void sync(ServerPlayer player) {
        CombatData data = get(player);

        int[] claimed = data.getClaimedRewards()
                .stream()
                .mapToInt(i -> i)
                .toArray();

        NetworkHandler.sendToPlayer(
                new CombatSyncPacket(
                        data.getLevel(),
                        data.getXpIntoLevel(),
                        claimed
                ),
                player
        );
    }
}
