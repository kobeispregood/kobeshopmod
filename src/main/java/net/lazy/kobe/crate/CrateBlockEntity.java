package net.lazy.kobe.crate;

import net.lazy.kobe.network.CrateRevealPacket;
import net.lazy.kobe.network.NetworkHandler;
import net.minecraft.server.level.ServerPlayer;
import net.lazy.kobe.block.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;

public class CrateBlockEntity extends BlockEntity {

    /* =========================
       CORE STATE
       ========================= */

    private CrateState state = CrateState.IDLE;
    private int ticks = 0;
    private ItemStack displayItem = ItemStack.EMPTY;

    // 🔑 CRATE RARITY (SYNCED)
    private CrateType crateType = CrateType.COMMON;

    public CrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CRATE_BE.get(), pos, state);
    }

    /* =========================
       CRATE TYPE
       ========================= */

    public CrateType getCrateType() {
        return crateType;
    }

    public void setCrateType(CrateType type) {
        this.crateType = type;
        setChanged();

        // 🔥 Force immediate client sync + render refresh
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }


    /* =========================
       ANIMATION / OPENING
       ========================= */

    public void startOpening(ItemStack stack) {
        this.state = CrateState.OPENING;
        this.displayItem = stack.copy();
        this.ticks = 0;
        setChanged();
    }

    public void open(ServerPlayer player, CrateType type) {
        CrateReward reward = CrateLootData.roll(type);

        // Give reward server-side
        reward.give(player);

        // Send reveal info to client (DISPLAY ONLY)
        NetworkHandler.sendToPlayer(
                new CrateRevealPacket(
                        reward.getStack().getItem().builtInRegistryHolder().key().location().toString(),
                        reward.getStack().getCount(),
                        type.id
                ),
                player
        );

        System.out.println("[CRATE] Opened crate of type: " + type.id);
    }

    /* =========================
       TICK
       ========================= */

    public static void tick(Level level, BlockPos pos, BlockState state, CrateBlockEntity be) {
        if (level.isClientSide && be.state == CrateState.OPENING) {
            be.ticks++;
        }
    }

    public CrateState getState() {
        return state;
    }

    public int getTicks() {
        return ticks;
    }

    public ItemStack getDisplayItem() {
        return displayItem;
    }

    /* =========================
       CLIENT SYNC (NeoForge 1.21+)
       ========================= */
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, provider);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider provider) {
        loadAdditional(tag, provider);
    }

    /* =========================
       SAVE / LOAD
       ========================= */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);

        tag.putInt("Ticks", ticks);
        tag.putString("State", state.name());
        tag.putString("CrateType", crateType.name());

        if (!displayItem.isEmpty()) {
            tag.put("DisplayItem", displayItem.save(provider));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        ticks = tag.getInt("Ticks");

        if (tag.contains("State")) {
            state = CrateState.valueOf(tag.getString("State"));
        }

        if (tag.contains("CrateType")) {
            crateType = CrateType.valueOf(tag.getString("CrateType"));
        }

        if (tag.contains("DisplayItem")) {
            displayItem = ItemStack.parse(provider, tag.getCompound("DisplayItem"))
                    .orElse(ItemStack.EMPTY);
        }
    }
}
