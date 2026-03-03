package net.lazy.kobe.titles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.HashMap;
import java.util.Map;

public class TitleRegistry {

    private static final Map<String, Title> TITLES = new HashMap<>();

    public static final Title WARRIOR_I = register(
            new Title(
                    "warrior",
                    Component.literal("[Warrior] ")
                            .withStyle(ChatFormatting.RED)
            )
    );

    public static final Title MINER_I = register(
            new Title(
                    "miner",
                    Component.literal("[Miner] ")
                            .withStyle(ChatFormatting.GRAY)
            )
    );

    private static Title register(Title title) {
        TITLES.put(title.getId(), title);
        return title;
    }

    public static Title get(String id) {
        return TITLES.get(id);
    }
}