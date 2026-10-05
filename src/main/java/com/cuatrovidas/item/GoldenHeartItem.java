package com.cuatrovidas.item;

import com.cuatrovidas.LivesData;
import com.cuatrovidas.LivesManager;
import com.cuatrovidas.LivesSettings;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Corazón dorado: clic derecho para sumar una vida a la barra (hasta el máximo configurado). */
public class GoldenHeartItem extends Item {
    public GoldenHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResultHolder.pass(stack);
        }

        LivesData data = LivesData.get(serverPlayer.server);
        LivesSettings settings = data.settings();
        int current = data.getLives(serverPlayer.getUUID());

        if (current >= settings.maxLives) {
            serverPlayer.displayClientMessage(
                    Component.literal("Ya tienes el máximo de vidas (" + settings.maxLives + ")")
                            .withStyle(ChatFormatting.RED),
                    true);
            return InteractionResultHolder.fail(stack);
        }

        LivesManager.setLives(serverPlayer, current + settings.livesPerHeart);
        int now = data.getLives(serverPlayer.getUUID());

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        level.playSound(null, serverPlayer.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.2F);
        ((ServerLevel) level).sendParticles(ParticleTypes.HEART,
                serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ(),
                8, 0.4D, 0.4D, 0.4D, 0.02D);
        int gained = now - current;
        serverPlayer.displayClientMessage(
                Component.literal("+" + gained + (gained == 1 ? " vida" : " vidas") + " (tienes " + now + ")")
                        .withStyle(ChatFormatting.GOLD),
                true);

        return InteractionResultHolder.sidedSuccess(stack, false);
    }
}
