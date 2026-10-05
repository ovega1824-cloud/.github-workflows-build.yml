package com.cuatrovidas;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CuatroVidasMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + CuatroVidasMod.MODID))
                    .icon(() -> new ItemStack(ModItems.GOLDEN_HEART.get()))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.GOLDEN_HEART.get());
                        output.accept(ModItems.CONFIG_HEART.get());
                    })
                    .build());

    private ModCreativeTabs() {
    }
}
