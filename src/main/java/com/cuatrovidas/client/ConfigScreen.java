package com.cuatrovidas.client;

import com.cuatrovidas.LivesSettings;
import com.cuatrovidas.network.ModNetwork;
import com.cuatrovidas.network.UpdateConfigPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;

/** Menú del corazón con tuerca: configura todas las opciones del mod. */
public class ConfigScreen extends Screen {
    private static final int ROW_HEIGHT = 24;
    private static final String[] LABELS = {
            "Vidas iniciales",
            "Vidas máximas",
            "Vidas por corazón dorado",
            "Corazón dorado en PvP",
            "Vida con cualquier muerte",
            "Mostrar barra de vidas"
    };

    private final LivesSettings settings;
    private boolean confirmReset = false;
    private int top;

    public ConfigScreen(LivesSettings settings) {
        super(Component.literal("Cuatro Vidas"));
        this.settings = settings.copy();
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        this.top = Math.max(30, this.height / 2 - 100);

        addStepper(cx, 0,
                () -> {
                    if (settings.startingLives > LivesSettings.MIN_LIVES) settings.startingLives--;
                },
                () -> {
                    if (settings.startingLives < LivesSettings.MAX_LIVES_LIMIT) {
                        settings.startingLives++;
                        if (settings.maxLives < settings.startingLives) settings.maxLives = settings.startingLives;
                    }
                });

        addStepper(cx, 1,
                () -> {
                    if (settings.maxLives > LivesSettings.MIN_LIVES) {
                        settings.maxLives--;
                        if (settings.startingLives > settings.maxLives) settings.startingLives = settings.maxLives;
                    }
                },
                () -> {
                    if (settings.maxLives < LivesSettings.MAX_LIVES_LIMIT) settings.maxLives++;
                });

        addStepper(cx, 2,
                () -> {
                    if (settings.livesPerHeart > 1) settings.livesPerHeart--;
                },
                () -> {
                    if (settings.livesPerHeart < LivesSettings.MAX_PER_HEART) settings.livesPerHeart++;
                });

        addToggle(cx, 3, () -> settings.pvpHeartDrop = !settings.pvpHeartDrop, () -> settings.pvpHeartDrop);
        addToggle(cx, 4, () -> settings.loseLifeOnAnyDeath = !settings.loseLifeOnAnyDeath,
                () -> settings.loseLifeOnAnyDeath);
        addToggle(cx, 5, () -> settings.hudVisible = !settings.hudVisible, () -> settings.hudVisible);

        int bottomY = top + LABELS.length * ROW_HEIGHT + 8;

        addRenderableWidget(Button.builder(Component.literal("Guardar"), button -> {
            ModNetwork.CHANNEL.sendToServer(new UpdateConfigPacket(settings.copy(), false));
            onClose();
        }).bounds(cx - 102, bottomY, 100, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Cancelar"), button -> onClose())
                .bounds(cx + 2, bottomY, 100, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Restablecer vidas de todos"), button -> {
            if (!confirmReset) {
                confirmReset = true;
                button.setMessage(Component.literal("¿Seguro? Pulsa otra vez").withStyle(ChatFormatting.RED));
            } else {
                ModNetwork.CHANNEL.sendToServer(new UpdateConfigPacket(settings.copy(), true));
                onClose();
            }
        }).bounds(cx - 102, bottomY + 24, 204, 20).build());
    }

    private void addStepper(int cx, int row, Runnable decrease, Runnable increase) {
        int y = top + row * ROW_HEIGHT;
        addRenderableWidget(Button.builder(Component.literal("-"), button -> decrease.run())
                .bounds(cx + 30, y, 20, 20).build());
        addRenderableWidget(Button.builder(Component.literal("+"), button -> increase.run())
                .bounds(cx + 110, y, 20, 20).build());
    }

    private void addToggle(int cx, int row, Runnable flip, BooleanSupplier state) {
        int y = top + row * ROW_HEIGHT;
        addRenderableWidget(Button.builder(toggleText(state.getAsBoolean()), button -> {
            flip.run();
            button.setMessage(toggleText(state.getAsBoolean()));
        }).bounds(cx + 30, y, 100, 20).build());
    }

    private static Component toggleText(boolean on) {
        return Component.literal(on ? "Sí" : "No")
                .withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);

        int cx = this.width / 2;
        graphics.drawCenteredString(this.font, "Configuración de Cuatro Vidas", cx, top - 18, 0xFFD700);

        for (int i = 0; i < LABELS.length; i++) {
            graphics.drawString(this.font, LABELS[i], cx - 170, top + i * ROW_HEIGHT + 6, 0xFFFFFF);
        }

        graphics.drawCenteredString(this.font, String.valueOf(settings.startingLives), cx + 80, top + 6, 0xFFFFFF);
        graphics.drawCenteredString(this.font, String.valueOf(settings.maxLives),
                cx + 80, top + ROW_HEIGHT + 6, 0xFFFFFF);
        graphics.drawCenteredString(this.font, String.valueOf(settings.livesPerHeart),
                cx + 80, top + 2 * ROW_HEIGHT + 6, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
