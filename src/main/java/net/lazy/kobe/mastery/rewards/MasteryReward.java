package net.lazy.kobe.mastery.rewards;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MasteryReward {

    public final int money;
    public final int xp;
    public final List<ItemStack> items;

    // ✅ ADD THESE
    public final PerkData perk;
    public final TitleData title;
    public final String infoDescription;

    private MasteryReward(
            int money,
            int xp,
            List<ItemStack> items,
            PerkData perk,
            TitleData title,
            String infoDescription
    ) {
        this.money = money;
        this.xp = xp;
        this.items = items;
        this.perk = perk;
        this.title = title;
        this.infoDescription = infoDescription;
    }

    public static MasteryReward fromJson(JsonObject obj) {

        int money = obj.has("money") ? obj.get("money").getAsInt() : 0;
        int xp = obj.has("xp") ? obj.get("xp").getAsInt() : 0;

        List<ItemStack> items = new ArrayList<>();

        if (obj.has("items")) {
            JsonArray arr = obj.getAsJsonArray("items");
            for (var e : arr) {
                JsonObject it = e.getAsJsonObject();
                ResourceLocation itemId =
                        ResourceLocation.parse(it.get("id").getAsString());

                int count = it.has("count")
                        ? it.get("count").getAsInt()
                        : 1;

                items.add(
                        new ItemStack(
                                BuiltInRegistries.ITEM.get(itemId),
                                count
                        )
                );
            }
        }

        // =============================
        // PERK
        // =============================
        PerkData perk = null;
        if (obj.has("perk")) {
            JsonObject p = obj.getAsJsonObject("perk");
            perk = new PerkData(
                    p.get("id").getAsString(),
                    p.get("name").getAsString(),
                    p.has("description") ? p.get("description").getAsString() : ""
            );
        }
        // =============================
        // INFO (NON-PERK TEXT)
        // =============================
        String infoDescription = null;
        if (obj.has("info")) {
            JsonObject info = obj.getAsJsonObject("info");
            if (info.has("description")) {
                infoDescription = info.get("description").getAsString();
            }
        }


        // =============================
        // TITLE
        // =============================
        TitleData title = null;
        if (obj.has("title")) {
            JsonObject t = obj.getAsJsonObject("title");
            title = new TitleData(
                    t.get("id").getAsString(),
                    t.get("name").getAsString(),
                    t.has("color") ? t.get("color").getAsString() : "gray"
            );
        }

        return new MasteryReward(money, xp, items, perk, title, infoDescription);
    }
}
