package com.cuatrovidas.network;

import com.cuatrovidas.CuatroVidasMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CuatroVidasMod.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private ModNetwork() {
    }

    public static void register() {
        int id = 0;
        CHANNEL.registerMessage(id++, SyncLivesPacket.class,
                SyncLivesPacket::encode, SyncLivesPacket::decode, SyncLivesPacket::handle);
        CHANNEL.registerMessage(id++, OpenConfigPacket.class,
                OpenConfigPacket::encode, OpenConfigPacket::decode, OpenConfigPacket::handle);
        CHANNEL.registerMessage(id++, UpdateConfigPacket.class,
                UpdateConfigPacket::encode, UpdateConfigPacket::decode, UpdateConfigPacket::handle);
    }
}
