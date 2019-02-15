package me.dmillerw.minions.item;

import me.dmillerw.minions.lib.ModInfo;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(ModInfo.ID)
public class ModItems {

    public static final ItemDebug debug = null;
    public static final ItemWorkbook workbook = null;

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    new ItemDebug().setRegistryName(ModInfo.ID, "debug"),
                    new ItemWorkbook().setRegistryName(ModInfo.ID, "workbook")
            );
        }
    }
}
