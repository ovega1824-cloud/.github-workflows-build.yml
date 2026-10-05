package com.cuatrovidas.client;

import net.minecraft.util.Mth;

/** Último estado de vidas recibido del servidor (solo datos, sin clases de cliente). */
public final class ClientState {
    public static int lives = 4;
    public static int startingLives = 4;
    public static int maxLives = 6;
    public static boolean hudVisible = true;

    private ClientState() {
    }

    public static void update(int newLives, int newStarting, int newMax, boolean newHudVisible) {
        lives = newLives;
        startingLives = newStarting;
        maxLives = newMax;
        hudVisible = newHudVisible;
    }

    /** Cuántos corazones se dibujan: las vidas iniciales, o más si ganó extras, sin pasar del máximo. */
    public static int slots() {
        return Mth.clamp(Math.max(startingLives, lives), 1, Math.max(1, maxLives));
    }
}
