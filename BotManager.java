package pl.example.botmod;

import java.util.Comparator;
import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

public final class BotManager {
    private static final double REMOVE_RANGE = 16.0;

    private BotManager() {}

    public static void handleAction(ServerPlayerEntity player, int action) {
        if (!(player.getWorld() instanceof ServerWorld world)) return;
        switch (action) {
            case BotNetworking.CREATE -> create(world, player);
            case BotNetworking.FOLLOW -> setFollowing(world, player, true);
            case BotNetworking.STOP -> setFollowing(world, player, false);
            case BotNetworking.REMOVE_NEAREST -> removeNearest(world, player);
            default -> { }
        }
    }

    private static List<BotEntity> ownedBots(ServerWorld world, ServerPlayerEntity player) {
        return world.getEntitiesByClass(
                BotEntity.class,
                player.getBoundingBox().expand(128.0),
                bot -> player.getUuid().equals(bot.getOwnerUuid())
        );
    }

    private static void create(ServerWorld world, ServerPlayerEntity player) {
        if (ownedBots(world, player).size() >= 20) {
            player.sendMessage(Text.literal("Masz już limit 20 botów."), true);
            return;
        }

        BotEntity bot = BotMod.BOT_ENTITY.create(world);
        if (bot == null) return;

        bot.setOwner(player.getUuid());
        bot.refreshPositionAndAngles(
                player.getX() + 1.5, player.getY(), player.getZ(),
                player.getYaw(), 0.0f
        );

        if (world.spawnEntity(bot)) {
            player.sendMessage(Text.literal("Utworzono bota."), true);
        }
    }

    private static void setFollowing(ServerWorld world, ServerPlayerEntity player, boolean following) {
        for (BotEntity bot : ownedBots(world, player)) {
            bot.setFollowing(following);
        }
        player.sendMessage(
                Text.literal(following ? "Boty podążają za tobą." : "Boty zostały zatrzymane."),
                true
        );
    }

    private static void removeNearest(ServerWorld world, ServerPlayerEntity player) {
        BotEntity nearest = ownedBots(world, player).stream()
                .filter(bot -> bot.squaredDistanceTo(player) <= REMOVE_RANGE * REMOVE_RANGE)
                .min(Comparator.comparingDouble(bot -> bot.squaredDistanceTo(player)))
                .orElse(null);

        if (nearest == null) {
            player.sendMessage(Text.literal("Nie znaleziono twojego bota w pobliżu."), true);
            return;
        }

        nearest.discard();
        player.sendMessage(Text.literal("Usunięto najbliższego bota."), true);
    }
}
