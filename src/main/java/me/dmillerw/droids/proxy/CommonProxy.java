package me.dmillerw.droids.proxy;

import me.dmillerw.droids.Droids;
import me.dmillerw.droids.common.ModInfo;
import me.dmillerw.droids.common.block.*;
import me.dmillerw.droids.common.entity.EntityDroid;
import me.dmillerw.droids.common.event.WorldEventHandler;
import me.dmillerw.droids.common.helper.DroidTracker;
import me.dmillerw.droids.common.network.PacketHandler;
import me.dmillerw.droids.common.tile.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.initialize();

        MinecraftForge.EVENT_BUS.register(WorldEventHandler.class);
        MinecraftForge.EVENT_BUS.register(DroidTracker.class);

        GameRegistry.registerTileEntity(TileAIController.class, new ResourceLocation(ModInfo.ID, BlockAIController.NAME));
        GameRegistry.registerTileEntity(TileChargingStation.class, new ResourceLocation(ModInfo.ID, BlockChargingStation.NAME));
        GameRegistry.registerTileEntity(TileFluidController.class, new ResourceLocation(ModInfo.ID, BlockFluidController.NAME));
        GameRegistry.registerTileEntity(TileItemController.class, new ResourceLocation(ModInfo.ID, BlockItemController.NAME));
        GameRegistry.registerTileEntity(TilePowerController.class, new ResourceLocation(ModInfo.ID, BlockPowerController.NAME));
        GameRegistry.registerTileEntity(TileRangeExtender.class, new ResourceLocation(ModInfo.ID, BlockRangeExtender.NAME));
        GameRegistry.registerTileEntity(TileZoneController.class, new ResourceLocation(ModInfo.ID, BlockZoneController.NAME));

        EntityRegistry.registerModEntity(
                new ResourceLocation(ModInfo.ID, "droid"),
                EntityDroid.class,
                "droid",
                1,
                Droids.INSTANCE,
                64,
                3,
                true,
                0,
                1);
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {

    }
}
