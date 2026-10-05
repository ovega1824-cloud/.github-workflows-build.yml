package com.cuatrovidas.network;

import com.cuatrovidas.client.ClientState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Servidor -> cliente: vidas actuales del jugador y datos para dibujar la barra. */
public record SyncLivesPacket(int lives, int startingLives, int maxLives, boolean hudVisible) {

    public static void encode(SyncLivesPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.lives);
        buf.writeInt(packet.startingLives);
        buf.writeInt(packet.maxLives);
        buf.writeBoolean(packet.hudVisible);
    }

    public static SyncLivesPacket decode(FriendlyByteBuf buf) {
        return new SyncLivesPacket(buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(SyncLivesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> ClientState.update(
                packet.lives, packet.startingLives, packet.maxLives, packet.hudVisible));
        context.setPacketHandled(true);
    }
}
