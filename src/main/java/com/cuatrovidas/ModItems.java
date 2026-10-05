package com.cuatrovidas;

import com.cuatrovidas.item.ConfigHeartItem;
import com.cuatrovidas.item.GoldenHeartItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CuatroVidasMod.MODID);

    /** Corazón dorado: lo dropean los jugadores al morir en PvP, clic derecho = +1 vida. */
    public static final RegistryObject<Item> GOLDEN_HEART =
            ITEMS.register("golden_heart", () -> new GoldenHeartItem(new Item.Properties()));

    /** Corazón con tuerca: abre el menú de configuración del mod. */
    public static final RegistryObject<Item> CONFIG_HEART =
            ITEMS.register("config_heart", () -> new ConfigHeartItem(new Item.Properties().stacksTo(1)));

    private ModItems() {
    }
}
