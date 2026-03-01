package net.lazy.kobe.curios;

import net.lazy.kobe.item.ModItems;
import net.lazy.kobe.menu.BlakeyBagData;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public final class CurioHelper {

    private CurioHelper() {}

    /* =========================================================
     * DUNHAM DICE
     * ========================================================= */
    public static boolean hasDunhamDice(ServerPlayer player) {
        var invOpt = CuriosApi.getCuriosInventory(player);
        if (invOpt.isEmpty()) return false;

        var inv = invOpt.get();

        // Normal equip
        if (inv.findFirstCurio(ModItems.DUNHAMDICE.get()).isPresent()) {
            return true;
        }

        // Inside Blakey Bag
        return inv.findFirstCurio(ModItems.BLAKEY_BAG.get())
                .map(curio -> {
                    var items = BlakeyBagData.getItems(
                            curio.stack(),
                            player.level().registryAccess()
                    );

                    for (ItemStack stack : items) {
                        if (stack.is(ModItems.DUNHAMDICE.get())) {
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    /* =========================================================
     * JESS'S JACKPOT
     * ========================================================= */
    public static boolean hasJessJackpot(ServerPlayer player) {
        var invOpt = CuriosApi.getCuriosInventory(player);
        if (invOpt.isEmpty()) return false;

        var inv = invOpt.get();

        // Normal equip
        if (inv.findFirstCurio(ModItems.JESS_JACKPOT.get()).isPresent()) {
            return true;
        }

        // Inside Blakey Bag
        return inv.findFirstCurio(ModItems.BLAKEY_BAG.get())
                .map(curio -> {
                    var items = BlakeyBagData.getItems(
                            curio.stack(),
                            player.level().registryAccess()
                    );

                    for (ItemStack stack : items) {
                        if (stack.is(ModItems.JESS_JACKPOT.get())) {
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    /* =========================================================
     * GENERIC (EQUIPPED ONLY)
     * ========================================================= */
    public static boolean hasCurio(ServerPlayer player, Item item) {
        return CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.findFirstCurio(item).isPresent())
                .orElse(false);
    }

    /* =========================================================
     * SHOP DISCOUNT
     * ========================================================= */
    public static float getShopDiscount(ServerPlayer player) {
        var invOpt = CuriosApi.getCuriosInventory(player);
        if (invOpt.isEmpty()) return 0.0f;

        var inv = invOpt.get();
        HolderLookup.Provider provider = player.level().registryAccess();

        // Normal equip has priority
        if (inv.findFirstCurio(ModItems.DINOS_DOLLAR.get()).isPresent()) {
            return 0.05f;
        }

        // Otherwise check Blakey Bag
        boolean hasDollarInBag = inv.findFirstCurio(ModItems.BLAKEY_BAG.get())
                .map(curio -> BlakeyBagData.contains(
                        curio.stack(),
                        ModItems.DINOS_DOLLAR.get(),
                        provider
                ))
                .orElse(false);

        return hasDollarInBag ? 0.05f : 0.0f;
    }
    public static boolean hasCurioClient(net.minecraft.world.entity.player.Player player, Item item) {
        return CuriosApi.getCuriosInventory(player)
                .map(inv -> inv.findFirstCurio(item).isPresent())
                .orElse(false);
    }
    // CLIENT ONLY — tooltip use
    public static boolean hasDinosDollarClient(net.minecraft.client.player.LocalPlayer player) {
        return top.theillusivec4.curios.api.CuriosApi
                .getCuriosInventory(player)
                .map(inv -> {
                    // Equipped
                    if (inv.findFirstCurio(ModItems.DINOS_DOLLAR.get()).isPresent()) {
                        return true;
                    }

                    // Inside Blakey Bag
                    return inv.findFirstCurio(ModItems.BLAKEY_BAG.get())
                            .map(curio -> {
                                var items = net.lazy.kobe.menu.BlakeyBagData.getItems(
                                        curio.stack(),
                                        player.level().registryAccess()
                                );
                                for (ItemStack stack : items) {
                                    if (stack.is(ModItems.DINOS_DOLLAR.get())) {
                                        return true;
                                    }
                                }
                                return false;
                            })
                            .orElse(false);
                })
                .orElse(false);
    }
    public static ItemStack getStrengthShard(ServerPlayer player) {
        return CuriosApi.getCuriosInventory(player)
                .flatMap(inv -> inv.findFirstCurio(ModItems.STRENGTH_SHARD.get()))
                .map(slot -> slot.stack())
                .orElse(ItemStack.EMPTY);
    }
    public static java.util.List<ItemStack> getAllEquippedCurios(ServerPlayer player) {

        java.util.List<ItemStack> results = new java.util.ArrayList<>();

        var invOpt = top.theillusivec4.curios.api.CuriosApi.getCuriosInventory(player);
        if (invOpt.isEmpty()) return results;

        var inv = invOpt.get();
        var provider = player.level().registryAccess();

        // Normal equipped curios
        inv.getCurios().forEach((identifier, stacksHandler) -> {
            for (int i = 0; i < stacksHandler.getSlots(); i++) {
                ItemStack stack = stacksHandler.getStacks().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    results.add(stack);
                }
            }
        });

        // Check Blakey Bag contents
        inv.findFirstCurio(net.lazy.kobe.item.ModItems.BLAKEY_BAG.get())
                .ifPresent(slot -> {
                    var bagItems =
                            net.lazy.kobe.menu.BlakeyBagData.getItems(
                                    slot.stack(),
                                    provider
                            );

                    for (ItemStack stack : bagItems) {
                        if (!stack.isEmpty()) {
                            results.add(stack);
                        }
                    }
                });

        return results;
    }

    public static float getShopDiscount(Player player) {
        if (!(player instanceof ServerPlayer sp)) return 0.0f;
        return getShopDiscount(sp);
    }
}