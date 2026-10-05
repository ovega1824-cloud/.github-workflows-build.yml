package com.cuatrovidas.item;

import com.cuatrovidas.LivesData;
import com.cuatrovidas.LivesManager;
import com.cuatrovidas.network.ModNetwork;
import com.cuatrovidas.network.OpenConfigPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

/** Corazón con tuerca: abre el menú para configurar el mod (solo operadores). */
public class ConfigHeartItem extends Item {
    public ConfigHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        if (player instanceof ServerPlayer serverPlayer) {
            if (LivesManager.canConfigure(serverPlayer)) {
                ModNetwork.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new OpenConfigPacket(LivesData.get(serverPlayer.server).settings().copy()));
            } else {
                serverPlayer.displayClientMessage(
                        Component.literal("Solo los operadores pueden configurar el mod")
                                .withStyle(ChatFormatting.RED),
                        true);
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, false);
    }
}
