package net.lazy.kobe.world;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;

import java.util.UUID;

public class IslandCommands {

    private static final int ISLAND_SPACING = 800;

    // 25×25×25 island center offset
    private static final double CENTER_X = 12.5;
    private static final double CENTER_Y = 13.5;
    private static final double CENTER_Z = 12.5;

    // ============================================================
    // MAIN /is COMMAND REGISTRATION
    // ============================================================
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("is")

                .then(Commands.literal("create")
                        .executes(ctx -> createHandler(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("home")
                        .executes(ctx -> goHome(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("go")
                        .executes(ctx -> goHome(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("send")
                        .executes(ctx -> goHome(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("hub")
                        .executes(ctx -> goHub(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("spawn")
                        .executes(ctx -> goHub(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("sethome")
                        .executes(ctx -> setHome(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("setspawn")
                        .executes(ctx -> setVisitorSpawn(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("members")
                        .executes(ctx -> listMembers(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("reset")
                        .executes(ctx -> beginReset(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("info")
                        .executes(ctx -> info(ctx.getSource().getPlayerOrException())))

                .then(Commands.literal("visit")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .executes(ctx -> visitIsland(
                                        ctx.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(ctx, "player")))))

                .then(Commands.literal("trust")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .executes(ctx -> trustPlayer(
                                        ctx.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(ctx, "player")))))

                .then(Commands.literal("untrust")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .executes(ctx -> untrustPlayer(
                                        ctx.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(ctx, "player")))))

                .then(Commands.literal("coop")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .executes(ctx -> coopPlayer(
                                        ctx.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(ctx, "player")))))

                .then(Commands.literal("uncoop")
                        .then(Commands.argument("player", StringArgumentType.string())
                                .executes(ctx -> uncoopPlayer(
                                        ctx.getSource().getPlayerOrException(),
                                        StringArgumentType.getString(ctx, "player")))))

                .then(Commands.literal("lock")
                        .executes(ctx -> toggleLock(ctx.getSource().getPlayerOrException())));
    }

    // ============================================================
    // GLOBAL /hub COMMAND
    // ============================================================
    public static LiteralArgumentBuilder<CommandSourceStack> registerHub() {
        return Commands.literal("hub")
                .executes(ctx -> goHub(ctx.getSource().getPlayerOrException()));
    }

    public static LiteralArgumentBuilder<CommandSourceStack> registerSpawn() {
        return Commands.literal("spawn")
                .executes(ctx -> goHub(ctx.getSource().getPlayerOrException()));
    }

    // ============================================================
    // ISLAND CREATION
    // ============================================================
    private static int createHandler(ServerPlayer player) {
        if (PlayerIslandStorage.hasIslandFile(player)) {
            return goHome(player);
        }
        createNewIsland(player);
        return 1;
    }

    private static BlockPos islandOriginFor(UUID uuid) {
        int h = uuid.hashCode();

        int gx = (h & 0xFFFF) - 32768;
        int gz = ((h >>> 16) & 0xFFFF) - 32768;

        int x = gx * ISLAND_SPACING;
        int z = gz * ISLAND_SPACING;
        int y = 100;

        return new BlockPos(x, y, z);
    }

    private static void createNewIsland(ServerPlayer player) {
        ServerLevel level = player.server.getLevel(IslandDimension.ISLAND_WORLD);

        BlockPos origin = islandOriginFor(player.getUUID());
        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();

        PlayerIslandStorage.saveIsland(player, x, y, z);

        ResourceLocation id = IslandRegistry.getRandomIsland();
        var template = level.getStructureManager().getOrCreate(id);

        template.placeInWorld(
                level,
                new BlockPos(x, y, z),
                new BlockPos(x, y, z),
                new StructurePlaceSettings(),
                level.random,
                2
        );

        teleportToIsland(level, player, x, y, z);
        player.sendSystemMessage(Component.literal("§aYour island has been created!"));
    }

    // ============================================================
    // TELEPORT HELPERS
    // ============================================================
    private static void teleportToIsland(ServerLevel level, ServerPlayer player, int x, int y, int z) {
        player.teleportTo(
                level,
                x + CENTER_X,
                y + CENTER_Y,
                z + CENTER_Z,
                -90f,
                0f
        );
    }

    private static int goHome(ServerPlayer player) {
        if (!PlayerIslandStorage.hasIslandFile(player)) {
            player.sendSystemMessage(Component.literal("§cYou don't have an island."));
            return 0;
        }

        ServerLevel level = player.server.getLevel(IslandDimension.ISLAND_WORLD);
        BlockPos home = PlayerIslandStorage.getHome(player);

        player.teleportTo(level,
                home.getX() + 0.5,
                home.getY(),
                home.getZ() + 0.5,
                -90f, 0f);

        return 1;
    }

    private static int goHub(ServerPlayer player) {
        ServerLevel level = player.server.overworld();
        player.teleportTo(level, -72.5, 4, 0.5, 270f, 0f);
        player.sendSystemMessage(Component.literal("§aTeleported to Hub."));
        return 1;
    }

    // ============================================================
    // HOME & VISITOR SPAWN SETTING
    // ============================================================
    private static int setHome(ServerPlayer player) {
        PlayerIslandStorage.setHome(player, player.blockPosition());
        player.sendSystemMessage(Component.literal("§aHome set!"));
        return 1;
    }

    private static int setVisitorSpawn(ServerPlayer player) {
        PlayerIslandStorage.setSpawn(player, player.blockPosition());
        player.sendSystemMessage(Component.literal("§aVisitor spawn set!"));
        return 1;
    }

    // ============================================================
    // MEMBERS LIST
    // ============================================================
    private static int listMembers(ServerPlayer player) {
        if (!PlayerIslandStorage.hasIslandFile(player)) {
            player.sendSystemMessage(Component.literal("§cYou do not have an island."));
            return 0;
        }

        var data = PlayerIslandStorage.loadIsland(player);

        player.sendSystemMessage(Component.literal("§6===== §eIsland Members §6====="));
        player.sendSystemMessage(Component.literal("§aOwner: §f" + player.getName().getString()));
        player.sendSystemMessage(Component.literal("§eLocked: " + (data.locked ? "§cYes" : "§aNo")));

        if (data.trusted.isEmpty()) {
            player.sendSystemMessage(Component.literal("§eTrusted: §7None"));
        } else {
            player.sendSystemMessage(Component.literal("§eTrusted:"));
            for (String u : data.trusted)
                player.sendSystemMessage(Component.literal("  §6• §f" + getPlayerName(player, u)));
        }

        if (data.coop.isEmpty()) {
            player.sendSystemMessage(Component.literal("§eCoop: §7None"));
        } else {
            player.sendSystemMessage(Component.literal("§eCoop:"));
            for (String u : data.coop)
                player.sendSystemMessage(Component.literal("  §b• §f" + getPlayerName(player, u)));
        }

        return 1;
    }

    private static String getPlayerName(ServerPlayer viewer, String uuidStr) {
        try {
            UUID uuid = UUID.fromString(uuidStr);
            ServerPlayer online = viewer.server.getPlayerList().getPlayer(uuid);

            if (online != null)
                return online.getName().getString() + " §a(Online)";

            return "§7" + uuidStr.substring(0, 8) + "… §c(Offline)";
        } catch (Exception e) {
            return "§cInvalid UUID";
        }
    }

    // ============================================================
    // RESET SYSTEM
    // ============================================================
    private static int beginReset(ServerPlayer player) {
        PlayerIslandStorage.setPendingReset(player, true);
        player.sendSystemMessage(Component.literal("§cType §e12345 §cin chat to confirm reset."));
        return 1;
    }

    public static void confirmReset(ServerPlayer player) {
        PlayerIslandStorage.deleteIsland(player);
        createNewIsland(player);
        PlayerIslandStorage.setPendingReset(player, false);
    }

    // ============================================================
    // INFO
    // ============================================================
    private static int info(ServerPlayer player) {
        var data = PlayerIslandStorage.loadIsland(player);
        player.sendSystemMessage(Component.literal("§bIsland Coordinates:"));
        player.sendSystemMessage(Component.literal("§eX: " + data.x + "  Y: " + data.y + "  Z: " + data.z));
        return 1;
    }

    // ============================================================
    // VISIT
    // ============================================================
    private static int visitIsland(ServerPlayer visitor, String name) {
        ServerPlayer target = visitor.server.getPlayerList().getPlayerByName(name);

        if (target == null) {
            visitor.sendSystemMessage(Component.literal("§cPlayer not online."));
            return 0;
        }

        if (PlayerIslandStorage.isLocked(target) &&
                !PlayerIslandStorage.isTrusted(target, visitor.getUUID())) {
            visitor.sendSystemMessage(Component.literal("§cThat island is locked."));
            return 0;
        }

        var data = PlayerIslandStorage.loadIsland(target);
        ServerLevel level = visitor.server.getLevel(IslandDimension.ISLAND_WORLD);

        teleportToIsland(level, visitor, data.x, data.y, data.z);
        visitor.sendSystemMessage(Component.literal("§aVisiting " + name + "'s island"));
        return 1;
    }

    // ============================================================
    // TRUST / UNTRUST / COOP / UNCOOP
    // ============================================================
    private static int trustPlayer(ServerPlayer player, String name) {
        ServerPlayer target = player.server.getPlayerList().getPlayerByName(name);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§cPlayer not found."));
            return 0;
        }

        PlayerIslandStorage.addTrusted(player, target.getUUID());
        player.sendSystemMessage(Component.literal("§aTrusted " + name + "!"));
        return 1;
    }

    private static int untrustPlayer(ServerPlayer player, String name) {
        ServerPlayer target = player.server.getPlayerList().getPlayerByName(name);
        if (target == null) {
            player.sendSystemMessage(Component.literal("§cPlayer not found."));
            return 0;
        }

        PlayerIslandStorage.removeTrusted(player, target.getUUID());
        player.sendSystemMessage(Component.literal("§eRemoved trust from " + name));
        return 1;
    }

    private static int coopPlayer(ServerPlayer owner, String name) {
        ServerPlayer target = owner.server.getPlayerList().getPlayerByName(name);
        if (target == null) {
            owner.sendSystemMessage(Component.literal("§cPlayer not found."));
            return 1;
        }

        PlayerIslandStorage.addCoop(owner, target.getUUID());
        owner.sendSystemMessage(Component.literal("§a" + name + " is now a coop member."));
        return 1;
    }

    private static int uncoopPlayer(ServerPlayer owner, String name) {
        ServerPlayer target = owner.server.getPlayerList().getPlayerByName(name);
        if (target == null) {
            owner.sendSystemMessage(Component.literal("§cPlayer not found."));
            return 1;
        }

        PlayerIslandStorage.removeCoop(owner, target.getUUID());
        owner.sendSystemMessage(Component.literal("§eRemoved coop role from " + name));
        return 1;
    }

    // ============================================================
    // LOCK
    // ============================================================
    private static int toggleLock(ServerPlayer player) {
        boolean state = PlayerIslandStorage.toggleLock(player);

        player.sendSystemMessage(Component.literal(
                state ? "§cIsland locked." : "§aIsland unlocked."
        ));

        return 1;
    }
}
