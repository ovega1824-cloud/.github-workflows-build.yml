package com.cuatrovidas.client;

import com.cuatrovidas.LivesSettings;
import net.minecraft.client.Minecraft;

/** Punto de entrada para código que solo existe en el cliente. */
public final class ClientHooks {
    private ClientHooks() {
    }

    public static void openConfig(LivesSettings settings) {
        Minecraft.getInstance().setScreen(new ConfigScreen(settings));
    }
}
