package com.cuatrovidas;

import com.cuatrovidas.network.ModNetwork;
import com.cuatrovidas.network.SyncLivesPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.network.PacketDistributor;

import java.util.UUID;

/** Lógica compartida: sincronización con el cliente, modo espectador y configuración. */
public final class LivesManager {
    private LivesManager() {
    }

    /** Operadores (o el dueño del mundo en un solo jugador) pueden configurar el mod. */
    public static boolean canConfigure(ServerPlayer player) {
        return player.hasPermissions(2) || player.server.isSingleplayerOwner(player.getGameProfile());
    }

    public static void sync(ServerPlayer player) {
        LivesData data = LivesData.get(player.server);
        LivesSettings s = data.settings();
        ModNetwork.CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new SyncLivesPacket(data.getLives(player.getUUID()), s.startingLives, s.maxLives, s.hudVisible));
    }

    public static void syncAll(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sync(player);
        }
    }

    /**
     * Ajusta el modo de juego según las vidas: con 0 vidas pasa a espectador (y queda marcado como eliminado);
     * si estaba eliminado y ahora tiene vidas, vuelve a supervivencia.
     */
    public static void applyState(ServerPlayer player) {
        LivesData data = LivesData.get(player.server);
        UUID id = player.getUUID();
        if (data.getLives(id) <= 0) {
            data.markEliminated(id);
            if (player.gameMode.getGameModeForPlayer() != GameType.SPECTATOR) {
                player.setGameMode(GameType.SPECTATOR);
            }
        } else if (data.isEliminated(id)) {
            data.clearEliminated(id);
            player.setGameMode(GameType.SURVIVAL);
        }
    }

    public static void setLives(ServerPlayer player, int lives) {
        LivesData data = LivesData.get(player.server);
        data.setLives(player.getUUID(), lives);
        applyState(player);
        sync(player);
    }

    /** Aplica una configuración nueva enviada desde el menú. */
    public static void applyConfig(MinecraftServer server, LivesSettings newSettings, boolean resetAll, ServerPlayer sender) {
        LivesData data = LivesData.get(server);
        data.setSettings(newSettings);
        data.clampAll();
        if (resetAll) {
            data.resetAll();
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            applyState(player);
            sync(player);
        }
        sender.displayClientMessage(
                Component.literal(resetAll ? "Configuración guardada y vidas restablecidas" : "Configuración guardada")
                        .withStyle(ChatFormatting.GREEN),
                true);
    }
}
