package com.cuatrovidas;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.function.ToIntFunction;

@Mod.EventBusSubscriber(modid = CuatroVidasMod.MODID)
public final class ModEvents {
    private ModEvents() {
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        if (player.isSpectator()) {
            return;
        }

        LivesData data = LivesData.get(player.server);
        LivesSettings settings = data.settings();

        Entity killer = event.getSource().getEntity();
        boolean pvp = killer instanceof ServerPlayer && killer != player;

        // El corazón dorado solo lo suelta un jugador que muere a manos de otro jugador.
        if (pvp && settings.pvpHeartDrop) {
            player.spawnAtLocation(new ItemStack(ModItems.GOLDEN_HEART.get()));
        }

        if (!pvp && !settings.loseLifeOnAnyDeath) {
            return;
        }

        int remaining = Math.max(0, data.getLives(player.getUUID()) - 1);
        data.setLives(player.getUUID(), remaining);

        if (remaining > 0) {
            LivesManager.sync(player);
            return;
        }

        // Última vida perdida: no hay respawn, el jugador pasa directo a espectador en el sitio.
        event.setCanceled(true);
        eliminate(player, data);
    }

    private static void eliminate(ServerPlayer player, LivesData data) {
        data.markEliminated(player.getUUID());

        player.setHealth(player.getMaxHealth());
        player.clearFire();
        player.fallDistance = 0.0F;
        player.removeAllEffects();

        if (!player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) {
            player.getInventory().dropAll();
        }

        player.setGameMode(GameType.SPECTATOR);
        player.server.getPlayerList().broadcastSystemMessage(
                Component.literal(player.getGameProfile().getName() + " se quedó sin vidas y ahora es espectador")
                        .withStyle(ChatFormatting.RED),
                false);
        LivesManager.sync(player);
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            LivesManager.applyState(player);
            LivesManager.sync(player);
        }
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Red de seguridad: un jugador eliminado nunca se queda en supervivencia tras respawnear.
            LivesManager.applyState(player);
            LivesManager.sync(player);

            int lives = LivesData.get(player.server).getLives(player.getUUID());
            if (lives > 0) {
                player.displayClientMessage(
                        Component.literal("Te quedan " + lives + (lives == 1 ? " vida" : " vidas"))
                                .withStyle(ChatFormatting.YELLOW),
                        true);
            }
        }
    }

    @SubscribeEvent
    public static void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            LivesManager.sync(player);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("vidas")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("get")
                        .then(Commands.argument("jugador", EntityArgument.player())
                                .executes(ctx -> {
                                    ServerPlayer target = EntityArgument.getPlayer(ctx, "jugador");
                                    int lives = LivesData.get(ctx.getSource().getServer()).getLives(target.getUUID());
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            target.getGameProfile().getName() + " tiene " + lives
                                                    + (lives == 1 ? " vida" : " vidas")), false);
                                    return lives;
                                })))
                .then(Commands.literal("set")
                        .then(Commands.argument("jugadores", EntityArgument.players())
                                .then(Commands.argument("cantidad",
                                                IntegerArgumentType.integer(0, LivesSettings.MAX_LIVES_LIMIT))
                                        .executes(ctx -> apply(ctx,
                                                p -> IntegerArgumentType.getInteger(ctx, "cantidad"))))))
                .then(Commands.literal("add")
                        .then(Commands.argument("jugadores", EntityArgument.players())
                                .then(Commands.argument("cantidad",
                                                IntegerArgumentType.integer(-LivesSettings.MAX_LIVES_LIMIT,
                                                        LivesSettings.MAX_LIVES_LIMIT))
                                        .executes(ctx -> apply(ctx,
                                                p -> LivesData.get(p.server).getLives(p.getUUID())
                                                        + IntegerArgumentType.getInteger(ctx, "cantidad"))))))
                .then(Commands.literal("reset")
                        .then(Commands.argument("jugadores", EntityArgument.players())
                                .executes(ctx -> apply(ctx,
                                        p -> LivesData.get(p.server).settings().startingLives)))));
    }

    private static int apply(CommandContext<CommandSourceStack> ctx, ToIntFunction<ServerPlayer> newValue)
            throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EntityArgument.getPlayers(ctx, "jugadores");
        for (ServerPlayer target : targets) {
            LivesManager.setLives(target, newValue.applyAsInt(target));
        }
        ctx.getSource().sendSuccess(
                () -> Component.literal("Vidas actualizadas para " + targets.size() + " jugador(es)"), true);
        return targets.size();
    }
}
