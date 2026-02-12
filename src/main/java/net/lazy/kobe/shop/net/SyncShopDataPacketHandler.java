package net.lazy.kobe.shop.net;

import net.lazy.kobe.shop.ShopCategory;
import net.lazy.kobe.shop.ShopData;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;

public final class SyncShopDataPacketHandler {

    public static void handle(SyncShopDataPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {

            ShopData.CATEGORY_ITEMS.clear();
            ShopData.PRICE_BY_KEY.clear();

            packet.categoryItems().forEach((catId, list) -> {
                ShopCategory category = ShopCategory.valueOf(catId);
                if (category != null) {
                    ShopData.CATEGORY_ITEMS.put(category, new ArrayList<>(list));
                }
            });

            ShopData.PRICE_BY_KEY.putAll(packet.prices());

            System.out.println("[KobeShop] Client shop data synced");
        });
    }
}
