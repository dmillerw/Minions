package me.dmillerw.minions.proxy;

import me.dmillerw.minions.Minions;
import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.lib.ModInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(
                new ResourceLocation(ModInfo.ID, "minion"),
                EntityMinion.class,
                "minion",
                1,
                Minions.INSTANCE,
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
