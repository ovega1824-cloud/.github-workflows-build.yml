package com.cuatrovidas;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Datos del mundo: configuración, vidas de cada jugador y jugadores eliminados.
 * Se guardan en el mundo, así que sobreviven a muertes, reinicios y cambios de dimensión.
 */
public class LivesData extends SavedData {
    private static final String NAME = "cuatrovidas_data";

    private LivesSettings settings = new LivesSettings();
    private final Map<UUID, Integer> lives = new HashMap<>();
    private final Set<UUID> eliminated = new HashSet<>();

    public LivesData() {
    }

    public static LivesData get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(LivesData::load, LivesData::new, NAME);
    }

    public LivesSettings settings() {
        return settings;
    }

    public void setSettings(LivesSettings newSettings) {
        this.settings = newSettings.sanitize();
        setDirty();
    }

    public int getLives(UUID id) {
        Integer value = lives.get(id);
        if (value == null) {
            value = settings.startingLives;
            lives.put(id, value);
            setDirty();
        }
        return Math.max(0, Math.min(value, settings.maxLives));
    }

    public void setLives(UUID id, int amount) {
        lives.put(id, Math.max(0, Math.min(amount, settings.maxLives)));
        setDirty();
    }

    /** Recorta las vidas de todos al nuevo máximo. */
    public void clampAll() {
        for (Map.Entry<UUID, Integer> entry : lives.entrySet()) {
            entry.setValue(Math.max(0, Math.min(entry.getValue(), settings.maxLives)));
        }
        setDirty();
    }

    /** Todos (incluso los desconectados) vuelven a las vidas iniciales. */
    public void resetAll() {
        for (Map.Entry<UUID, Integer> entry : lives.entrySet()) {
            entry.setValue(settings.startingLives);
        }
        setDirty();
    }

    public boolean isEliminated(UUID id) {
        return eliminated.contains(id);
    }

    public void markEliminated(UUID id) {
        if (eliminated.add(id)) {
            setDirty();
        }
    }

    public void clearEliminated(UUID id) {
        if (eliminated.remove(id)) {
            setDirty();
        }
    }

    public static LivesData load(CompoundTag tag) {
        LivesData data = new LivesData();
        if (tag.contains("settings")) {
            data.settings = LivesSettings.load(tag.getCompound("settings"));
        }
        ListTag livesList = tag.getList("lives", Tag.TAG_COMPOUND);
        for (int i = 0; i < livesList.size(); i++) {
            CompoundTag entry = livesList.getCompound(i);
            if (entry.hasUUID("id")) {
                data.lives.put(entry.getUUID("id"), entry.getInt("amount"));
            }
        }
        ListTag eliminatedList = tag.getList("eliminated", Tag.TAG_STRING);
        for (int i = 0; i < eliminatedList.size(); i++) {
            try {
                data.eliminated.add(UUID.fromString(eliminatedList.getString(i)));
            } catch (IllegalArgumentException ignored) {
                // entrada corrupta: se ignora
            }
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("settings", settings.save());

        ListTag livesList = new ListTag();
        for (Map.Entry<UUID, Integer> entry : new ArrayList<>(lives.entrySet())) {
            CompoundTag item = new CompoundTag();
            item.putUUID("id", entry.getKey());
            item.putInt("amount", entry.getValue());
            livesList.add(item);
        }
        tag.put("lives", livesList);

        ListTag eliminatedList = new ListTag();
        for (UUID id : eliminated) {
            eliminatedList.add(StringTag.valueOf(id.toString()));
        }
        tag.put("eliminated", eliminatedList);
        return tag;
    }
}
