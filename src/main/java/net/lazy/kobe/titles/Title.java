package net.lazy.kobe.titles;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public class Title {

    private final String id;
    private final Component display;

    public Title(String id, Component display) {
        this.id = id;
        this.display = display;
    }

    public String getId() {
        return id;
    }

    public Component getDisplay() {
        return display;
    }
}