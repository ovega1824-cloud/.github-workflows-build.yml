package com.cuatrovidas.network;

import com.cuatrovidas.LivesManager;
import com.cuatrovidas.LivesSettings;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Cliente -> servidor: guarda la configuración (y opcionalmente restablece las vidas de todos). */
public record UpdateConfigPacket(LivesSettings settings, boolean resetAll) {

    public static void encode(UpdateConfigPacket packet, FriendlyByteBuf buf) {
        packet.settings.write(buf);
        buf.writeBoolean(packet.resetAll);
    }

    public static UpdateConfigPacket decode(FriendlyByteBuf buf) {
        return new UpdateConfigPacket(LivesSettings.read(buf), buf.readBoolean());
    }

    public static void handle(UpdateConfigPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            // El servidor valida siempre el permiso: un cliente modificado no puede cambiar la configuración.
            if (sender == null || !LivesManager.canConfigure(sender)) {
                return;
            }
            LivesManager.applyConfig(sender.server, packet.settings, packet.resetAll, sender);
        });
        context.setPacketHandled(true);
    }
}
