package net.lazy.kobe.shop.net;

import net.lazy.kobe.shop.PriceEntry;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public record SyncShopDataPacket(
        Map<String, List<ItemStack>> categoryItems,
        Map<String, PriceEntry> prices
) implements CustomPacketPayload {

    public static final Type<SyncShopDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath("kobe", "sync_shop_data"));

    // =============================================================
    //  STREAM CODEC (REQUIRED FOR 1.21.1)
    // =============================================================
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncShopDataPacket> STREAM_CODEC =
            StreamCodec.of(
                    SyncShopDataPacket::encode,
                    SyncShopDataPacket::decode
            );

    private static void encode(RegistryFriendlyByteBuf buf, SyncShopDataPacket pkt) {

        // ---- category items ----
        buf.writeInt(pkt.categoryItems.size());
        pkt.categoryItems.forEach((catId, list) -> {
            buf.writeUtf(catId);
            buf.writeInt(list.size());
            for (ItemStack stack : list) {
                ItemStack.STREAM_CODEC.encode(buf, stack);
            }
        });

        // ---- prices ----
        buf.writeInt(pkt.prices.size());
        pkt.prices.forEach((key, price) -> {
            buf.writeUtf(key);
            buf.writeDouble(price.buy());
            buf.writeDouble(price.sell());
        });
    }

    private static SyncShopDataPacket decode(RegistryFriendlyByteBuf buf) {

        // ---- category items ----
        int catCount = buf.readInt();
        Map<String, List<ItemStack>> categoryItems = new HashMap<>();

        for (int i = 0; i < catCount; i++) {
            String catId = buf.readUtf();
            int size = buf.readInt();

            List<ItemStack> list = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                list.add(ItemStack.STREAM_CODEC.decode(buf));
            }

            categoryItems.put(catId, list);
        }

        // ---- prices ----
        int priceCount = buf.readInt();
        Map<String, PriceEntry> prices = new HashMap<>();

        for (int i = 0; i < priceCount; i++) {
            String key = buf.readUtf();
            double buy = buf.readDouble();
            double sell = buf.readDouble();
            prices.put(key, new PriceEntry(buy, sell));
        }

        return new SyncShopDataPacket(categoryItems, prices);
    }
    public static SyncShopDataPacket fromServerState() {

        Map<String, List<ItemStack>> categoryItems = new LinkedHashMap<>();

        for (var category : net.lazy.kobe.shop.ShopCategory.values()) {
            categoryItems.put(
                    category.getId(),
                    new ArrayList<>(net.lazy.kobe.shop.ShopData.getItems(category))
            );
        }

        Map<String, PriceEntry> prices =
                new HashMap<>(net.lazy.kobe.shop.ShopData.PRICE_BY_KEY);

        return new SyncShopDataPacket(categoryItems, prices);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
