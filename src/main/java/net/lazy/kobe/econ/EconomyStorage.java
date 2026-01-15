package net.lazy.kobe.econ;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.*;

public class EconomyStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type TYPE = new TypeToken<Map<String, Integer>>(){}.getType();

    private static Map<UUID, Integer> balances = new HashMap<>();
    private static File saveFile;

    public static void init(MinecraftServer server) {
        // Correct NeoForge 1.21 path to the world root
        saveFile = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT)
                .resolve("data/kobe_economy.json")
                .toFile();

        load();
    }

    public static void load() {
        if (!saveFile.exists()) {
            balances = new HashMap<>();
            save();
            return;
        }

        try (FileReader reader = new FileReader(saveFile)) {
            Map<String, Integer> raw = GSON.fromJson(reader, TYPE);
            balances = new HashMap<>();

            if (raw != null) {
                for (var e : raw.entrySet()) {
                    balances.put(UUID.fromString(e.getKey()), e.getValue());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(saveFile)) {

            Map<String, Integer> raw = new HashMap<>();
            for (var e : balances.entrySet()) {
                raw.put(e.getKey().toString(), e.getValue());
            }

            GSON.toJson(raw, writer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int get(UUID id) {
        return balances.getOrDefault(id, 0);
    }

    public static void set(UUID id, int amount) {
        balances.put(id, amount);
        save();
    }

    public static Map<UUID, Integer> all() {
        return balances;
    }
}