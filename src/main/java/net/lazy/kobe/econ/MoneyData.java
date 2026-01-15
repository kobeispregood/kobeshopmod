package net.lazy.kobe.econ;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class MoneyData implements INBTSerializable<CompoundTag> {

    private int balance = 0;

    public int get() {
        return balance;
    }

    public void set(int amount) {
        this.balance = amount;
    }

    public void add(int amount) {
        this.balance += amount;
    }

    public void remove(int amount) {
        this.balance = Math.max(0, this.balance - amount);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("balance", balance);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        this.balance = tag.getInt("balance");
    }
}