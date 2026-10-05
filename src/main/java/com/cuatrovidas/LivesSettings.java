package com.cuatrovidas;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;

/** Opciones configurables del mod (se guardan en el mundo y se sincronizan con los clientes). */
public class LivesSettings {
    public static final int MIN_LIVES = 1;
    public static final int MAX_LIVES_LIMIT = 10;
    public static final int MAX_PER_HEART = 3;

    /** Vidas con las que empieza cada jugador. */
    public int startingLives = 4;
    /** Límite de vidas que se pueden tener en la barra (incluye las extra). */
    public int maxLives = 6;
    /** Cuántas vidas da cada corazón dorado. */
    public int livesPerHeart = 1;
    /** Si los jugadores sueltan un corazón dorado al morir en PvP. */
    public boolean pvpHeartDrop = true;
    /** Si se pierde una vida con cualquier muerte (false = solo muertes en PvP). */
    public boolean loseLifeOnAnyDeath = true;
    /** Si se muestra la barra de vidas en pantalla. */
    public boolean hudVisible = true;

    public LivesSettings copy() {
        LivesSettings c = new LivesSettings();
        c.startingLives = startingLives;
        c.maxLives = maxLives;
        c.livesPerHeart = livesPerHeart;
        c.pvpHeartDrop = pvpHeartDrop;
        c.loseLifeOnAnyDeath = loseLifeOnAnyDeath;
        c.hudVisible = hudVisible;
        return c;
    }

    /** Deja los valores dentro de rangos válidos. */
    public LivesSettings sanitize() {
        startingLives = Mth.clamp(startingLives, MIN_LIVES, MAX_LIVES_LIMIT);
        maxLives = Mth.clamp(maxLives, MIN_LIVES, MAX_LIVES_LIMIT);
        if (maxLives < startingLives) {
            maxLives = startingLives;
        }
        livesPerHeart = Mth.clamp(livesPerHeart, 1, MAX_PER_HEART);
        return this;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("startingLives", startingLives);
        tag.putInt("maxLives", maxLives);
        tag.putInt("livesPerHeart", livesPerHeart);
        tag.putBoolean("pvpHeartDrop", pvpHeartDrop);
        tag.putBoolean("loseLifeOnAnyDeath", loseLifeOnAnyDeath);
        tag.putBoolean("hudVisible", hudVisible);
        return tag;
    }

    public static LivesSettings load(CompoundTag tag) {
        LivesSettings s = new LivesSettings();
        if (tag.contains("startingLives")) s.startingLives = tag.getInt("startingLives");
        if (tag.contains("maxLives")) s.maxLives = tag.getInt("maxLives");
        if (tag.contains("livesPerHeart")) s.livesPerHeart = tag.getInt("livesPerHeart");
        if (tag.contains("pvpHeartDrop")) s.pvpHeartDrop = tag.getBoolean("pvpHeartDrop");
        if (tag.contains("loseLifeOnAnyDeath")) s.loseLifeOnAnyDeath = tag.getBoolean("loseLifeOnAnyDeath");
        if (tag.contains("hudVisible")) s.hudVisible = tag.getBoolean("hudVisible");
        return s.sanitize();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(startingLives);
        buf.writeInt(maxLives);
        buf.writeInt(livesPerHeart);
        buf.writeBoolean(pvpHeartDrop);
        buf.writeBoolean(loseLifeOnAnyDeath);
        buf.writeBoolean(hudVisible);
    }

    public static LivesSettings read(FriendlyByteBuf buf) {
        LivesSettings s = new LivesSettings();
        s.startingLives = buf.readInt();
        s.maxLives = buf.readInt();
        s.livesPerHeart = buf.readInt();
        s.pvpHeartDrop = buf.readBoolean();
        s.loseLifeOnAnyDeath = buf.readBoolean();
        s.hudVisible = buf.readBoolean();
        return s;
    }
}
