package com.cuatrovidas.network;

import com.cuatrovidas.LivesSettings;
import com.cuatrovidas.client.ClientHooks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Servidor -> cliente: abre el menú de configuración con los valores actuales. */
public record OpenConfigPacket(LivesSettings settings) {

    public static void encode(OpenConfigPacket packet, FriendlyByteBuf buf) {
        packet.settings.write(buf);
    }

    public static OpenConfigPacket decode(FriendlyByteBuf buf) {
        return new OpenConfigPacket(LivesSettings.read(buf));
    }

    public static void handle(OpenConfigPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientHooks.openConfig(packet.settings)));
        context.setPacketHandled(true);
    }
}
