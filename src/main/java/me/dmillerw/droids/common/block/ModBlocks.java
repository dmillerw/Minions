package me.dmillerw.droids.common.block;

import me.dmillerw.droids.common.ModInfo;
import me.dmillerw.droids.common.item.block.ItemBlockPlayerOwned;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(ModInfo.ID)
public class ModBlocks {

    public static final BlockAIController ai_controller = null;
    @GameRegistry.ObjectHolder(ModInfo.ID + ":ai_controller")
    public static ItemBlock ai_controller_item = null;

    public static final BlockRangeExtender range_extender = null;
    @GameRegistry.ObjectHolder(ModInfo.ID + ":range_extender")
    public static ItemBlock range_extender_item = null;

    public static final BlockChargingStation charging_station = null;
    @GameRegistry.ObjectHolder(ModInfo.ID + ":charging_station")
    public static ItemBlock charging_station_item = null;

    public static final BlockZoneController zone_controller = null;
    @GameRegistry.ObjectHolder(ModInfo.ID + ":zone_controller")
    public static ItemBlock zone_controller_item = null;

    public static final BlockItemController item_controller = null;
    @GameRegistry.ObjectHolder(ModInfo.ID + ":item_controller")
    public static ItemBlock item_controller_item = null;

    public static final BlockFluidController fluid_controller = null;
    @GameRegistry.ObjectHolder(ModInfo.ID + ":fluid_controller")
    public static ItemBlock fluid_controller_item = null;

    public static final BlockPowerController power_controller = null;
    @GameRegistry.ObjectHolder(ModInfo.ID + ":power_controller")
    public static ItemBlock power_controller_item = null;

    @Mod.EventBusSubscriber
    public static class RegistrationHandler {

        @SubscribeEvent
        public static void addBlocks(RegistryEvent.Register<Block> event) {
            event.getRegistry().registerAll(
                    new BlockAIController(),
                    new BlockRangeExtender(),
                    new BlockChargingStation(),
                    new BlockZoneController(),
                    new BlockItemController(),
                    new BlockFluidController(),
                    new BlockPowerController()
            );
        }

        @SubscribeEvent
        public static void addItems(RegistryEvent.Register<Item> event) {
            event.getRegistry().registerAll(
                    itemBlockOwned(ai_controller),
                    itemBlockOwned(range_extender),
                    itemBlockOwned(charging_station),
                    itemBlockOwned(zone_controller),
                    itemBlockOwned(item_controller),
                    itemBlockOwned(fluid_controller),
                    itemBlockOwned(power_controller)
            );
        }

        private static ItemBlock itemBlock(Block block) {
            return (ItemBlock) new ItemBlock(block).setRegistryName(block.getRegistryName());
        }

        private static ItemBlock itemBlockOwned(Block block) {
            return (ItemBlock) new ItemBlockPlayerOwned(block).setRegistryName(block.getRegistryName());
        }
    }
}
