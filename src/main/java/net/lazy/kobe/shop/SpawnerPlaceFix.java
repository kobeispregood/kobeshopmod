package net.lazy.kobe.shop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.minecraft.world.item.Items;
import net.minecraft.core.component.DataComponents;

public class SpawnerPlaceFix {

    public SpawnerPlaceFix() {
        NeoForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {

        if (!(event.getEntity() instanceof net.minecraft.world.entity.player.Player player))
            return;

        ItemStack used = player.getMainHandItem();
        if (used.isEmpty()) return;

        // Apply only to spawners
        if (!used.is(Items.SPAWNER)) return;

        var blockData = used.get(DataComponents.BLOCK_ENTITY_DATA);
        if (blockData == null) return;

        BlockPos pos = event.getPos();
        BlockEntity be = event.getLevel().getBlockEntity(pos);
        if (be == null) return;

        // ✔ Correct signature for your game version
        be.loadCustomOnly(blockData.copyTag(), event.getLevel().registryAccess());
        be.setChanged();
    }
}