package me.dmillerw.minions.proxy;

import me.dmillerw.minions.Minions;
import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.event.EntityEventHandler;
import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.tasks.TaskCollectItems;
import me.dmillerw.minions.tasks.TaskDummy;
import me.dmillerw.minions.tasks.TaskRegistry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class CommonProxy implements IProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        PacketHandler.initialize();

        MinecraftForge.EVENT_BUS.register(EntityEventHandler.class);

        TaskRegistry.registerTask(new TaskCollectItems());
        for (int i=0; i<10; i++)
            TaskRegistry.registerTask(new TaskDummy(i));

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
