package net.lazy.kobe.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.util.*;

public class PlayerIslandStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FOLDER_NAME = "kobe_islands";

    /** Full island save object */
    public static class IslandData {
        public int x;
        public int y;
        public int z;

        public boolean locked = false;
        public boolean pendingReset = false;

        public int homeX;
        public int homeY;
        public int homeZ;

        public int spawnX;
        public int spawnY;
        public int spawnZ;

        public List<String> trusted = new ArrayList<>();
        public List<String> coop = new ArrayList<>();

        public IslandData(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;

            // Defaults: home and spawn at island center
            this.homeX = x + 12;
            this.homeY = y + 14;
            this.homeZ = z + 12;

            this.spawnX = homeX;
            this.spawnY = homeY;
            this.spawnZ = homeZ;
        }
    }

    // ========================================
    // FILE HANDLING
    // ========================================
    private static File getFolder(ServerPlayer player) {
        File folder = new File(player.server.getWorldPath(LevelResource.ROOT).toFile(), FOLDER_NAME);
        if (!folder.exists()) folder.mkdirs();
        return folder;
    }

    private static File getFile(ServerPlayer player) {
        return new File(getFolder(player), player.getUUID() + ".json");
    }

    public static boolean hasIslandFile(ServerPlayer player) {
        return getFile(player).exists();
    }

    // ========================================
    // LOAD / SAVE ISLAND
    // ========================================
    public static void saveIsland(ServerPlayer player, int x, int y, int z) {
        IslandData data = loadIsland(player);
        if (data == null)
            data = new IslandData(x, y, z);

        data.x = x;
        data.y = y;
        data.z = z;

        writeIsland(player, data);
    }

    public static IslandData loadIsland(ServerPlayer player) {
        File file = getFile(player);
        if (!file.exists()) return null;

        try (FileReader r = new FileReader(file)) {
            IslandData data = GSON.fromJson(r, IslandData.class);

            if (data == null)
                return null;

            // ---------- Fix missing fields (old JSON compatibility) ----------

            if (data.trusted == null)
                data.trusted = new ArrayList<>();

            if (data.coop == null)
                data.coop = new ArrayList<>();

            if (data.spawnX == 0 && data.spawnY == 0 && data.spawnZ == 0) {
                // set default spawn at island center
                data.spawnX = data.x + 12;
                data.spawnY = data.y + 14;
                data.spawnZ = data.z + 12;
            }

            if (data.homeX == 0 && data.homeY == 0 && data.homeZ == 0) {
                // set default home at island center
                data.homeX = data.x + 12;
                data.homeY = data.y + 14;
                data.homeZ = data.z + 12;
            }

            // locked may be missing (default to false)
            // pendingReset may be missing (default false)

            writeIsland(player, data); // auto-upgrade file

            return data;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void writeIsland(ServerPlayer player, IslandData data) {
        try (FileWriter w = new FileWriter(getFile(player))) {
            GSON.toJson(data, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========================================
    // DELETE ISLAND
    // ========================================
    public static void deleteIsland(ServerPlayer player) {
        File file = getFile(player);
        if (file.exists()) file.delete();
    }

    // ========================================
    // PENDING RESET (12345 system)
    // ========================================
    public static void setPendingReset(ServerPlayer player, boolean state) {
        IslandData data = loadIsland(player);
        if (data == null) return;
        data.pendingReset = state;
        writeIsland(player, data);
    }

    public static boolean isPendingReset(ServerPlayer player) {
        IslandData data = loadIsland(player);
        return data != null && data.pendingReset;
    }

    // ========================================
    // LOCK / UNLOCK
    // ========================================
    public static boolean toggleLock(ServerPlayer player) {
        IslandData data = loadIsland(player);
        if (data == null) return false;

        data.locked = !data.locked;
        writeIsland(player, data);
        return data.locked;
    }

    public static boolean isLocked(ServerPlayer owner) {
        IslandData data = loadIsland(owner);
        return data != null && data.locked;
    }

    // ========================================
    // TRUST SYSTEM
    // ========================================
    public static void addTrusted(ServerPlayer owner, UUID trustedPlayer) {
        IslandData data = loadIsland(owner);
        if (data == null) return;

        String uuid = trustedPlayer.toString();
        if (!data.trusted.contains(uuid))
            data.trusted.add(uuid);

        writeIsland(owner, data);
    }

    public static void removeTrusted(ServerPlayer owner, UUID trustedPlayer) {
        IslandData data = loadIsland(owner);
        if (data == null) return;

        data.trusted.remove(trustedPlayer.toString());
        writeIsland(owner, data);
    }

    public static boolean isTrusted(ServerPlayer owner, UUID visitor) {
        IslandData data = loadIsland(owner);
        return data != null && data.trusted.contains(visitor.toString());
    }

    // ========================================
    // COOP SYSTEM (temporary)
    // ========================================
    public static void addCoop(ServerPlayer owner, UUID coopPlayer) {
        IslandData data = loadIsland(owner);
        if (data == null) return;

        String uuid = coopPlayer.toString();
        if (!data.coop.contains(uuid))
            data.coop.add(uuid);

        writeIsland(owner, data);
    }

    public static void removeCoop(ServerPlayer owner, UUID coopPlayer) {
        IslandData data = loadIsland(owner);
        if (data == null) return;

        data.coop.remove(coopPlayer.toString());
        writeIsland(owner, data);
    }

    public static boolean isCoop(ServerPlayer owner, UUID visitor) {
        IslandData data = loadIsland(owner);
        return data != null && data.coop.contains(visitor.toString());
    }

    public static void clearCoopOnLogout(UUID playerUUID) {
        // Iterate through all island files? (optional)
        // Skip for now unless you want coop to auto-clear on logout.
    }

    // ========================================
    // HOME / VISITOR SPAWN
    // ========================================
    public static void setHome(ServerPlayer owner, BlockPos pos) {
        IslandData data = loadIsland(owner);
        if (data == null) return;

        data.homeX = pos.getX();
        data.homeY = pos.getY();
        data.homeZ = pos.getZ();

        writeIsland(owner, data);
    }

    public static BlockPos getHome(ServerPlayer owner) {
        IslandData data = loadIsland(owner);
        if (data == null) return null;

        return new BlockPos(data.homeX, data.homeY, data.homeZ);
    }

    public static void setSpawn(ServerPlayer owner, BlockPos pos) {
        IslandData data = loadIsland(owner);
        if (data == null) return;

        data.spawnX = pos.getX();
        data.spawnY = pos.getY();
        data.spawnZ = pos.getZ();

        writeIsland(owner, data);
    }

    public static BlockPos getSpawn(ServerPlayer owner) {
        IslandData data = loadIsland(owner);
        if (data == null) return null;

        return new BlockPos(data.spawnX, data.spawnY, data.spawnZ);
    }
}