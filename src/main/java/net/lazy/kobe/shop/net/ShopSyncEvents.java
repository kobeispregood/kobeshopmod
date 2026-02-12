package net.lazy.kobe.shop.net;

import net.lazy.kobe.network.NetworkHandler;
import net.lazy.kobe.shop.*;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.*;

public final class ShopSyncEvents {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        Map<String, List<ItemStack>> categoryItems = new HashMap<>();
        for (ShopCategory category : ShopCategory.values()) {
            List<ItemStack> list = ShopData.CATEGORY_ITEMS.get(category);
            if (list != null) {
                categoryItems.put(category.getId(), List.copyOf(list));
            }
        }

        SyncShopDataPacket packet = new SyncShopDataPacket(
                categoryItems,
                Map.copyOf(ShopData.PRICE_BY_KEY)
        );

        // 🔑 CORRECT ORDER
        NetworkHandler.sendToPlayer(packet, player);

        System.out.println("[KobeShop] Synced shop data to " + player.getName().getString());
    }
}
