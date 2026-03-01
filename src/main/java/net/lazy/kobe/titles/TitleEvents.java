package net.lazy.kobe.titles;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class TitleEvents {

    @SubscribeEvent
    public void onNameFormat(PlayerEvent.NameFormat event) {

        var player = event.getEntity();
        var data = player.getData(TitleAttachment.TITLES);

        if (data == null) return;

        String selected = data.getSelected();
        if (selected == null) return;

        Title title = TitleRegistry.get(selected);
        if (title == null) return;

        event.setDisplayname(
                Component.empty()
                        .append(title.getDisplay())
                        .append(player.getName())
        );
    }
}