package com.cuatrovidas.client;

import com.cuatrovidas.CuatroVidasMod;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/** Barra de corazones en la parte superior central de la pantalla. */
public final class LivesOverlay {
    private static final ResourceLocation FULL =
            new ResourceLocation(CuatroVidasMod.MODID, "textures/gui/heart_full.png");
    private static final ResourceLocation EMPTY =
            new ResourceLocation(CuatroVidasMod.MODID, "textures/gui/heart_empty.png");

    private static final int SIZE = 16;
    private static final int GAP = 3;
    private static final int TOP = 4;

    public static final IGuiOverlay OVERLAY = (gui, graphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (!ClientState.hudVisible || mc.player == null || mc.options.hideGui) {
            return;
        }

        int slots = ClientState.slots();
        int total = slots * SIZE + (slots - 1) * GAP;
        int x = (width - total) / 2;

        RenderSystem.enableBlend();
        for (int i = 0; i < slots; i++) {
            ResourceLocation texture = i < ClientState.lives ? FULL : EMPTY;
            graphics.blit(texture, x + i * (SIZE + GAP), TOP, SIZE, SIZE, 0.0F, 0.0F, 32, 32, 32, 32);
        }
        RenderSystem.disableBlend();
    };

    private LivesOverlay() {
    }
}
