package pl.example.botmod;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public final class BotNetworking {
    private BotNetworking() {}

    public static final CustomPayload.Id<ActionPayload> ACTION_ID =
            new CustomPayload.Id<>(Identifier.of(BotMod.MOD_ID, "bot_action"));

    public record ActionPayload(int action) implements CustomPayload {
        public static final PacketCodec<RegistryByteBuf, ActionPayload> CODEC =
                PacketCodec.tuple(PacketCodecs.VAR_INT, ActionPayload::action, ActionPayload::new);

        @Override
        public Id<? extends CustomPayload> getId() {
            return ACTION_ID;
        }
    }

    public static final int CREATE = 0;
    public static final int FOLLOW = 1;
    public static final int STOP = 2;
    public static final int REMOVE_NEAREST = 3;

    public static void register() {
        PayloadTypeRegistry.playC2S().register(ACTION_ID, ActionPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ACTION_ID, (payload, context) -> {
            var player = context.player();
            if (!player.isAlive() || player.isSpectator()) return;
            BotManager.handleAction(player, payload.action());
        });
    }

    public static void sendAction(int action) {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(
                new ActionPayload(action)
        );
    }
}
