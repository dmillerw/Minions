package me.dmillerw.minions.item;

import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.tasks.ParameterMap;
import me.dmillerw.minions.tasks.TaskInstance;
import me.dmillerw.minions.tasks.TaskRegistry;
import me.dmillerw.minions.tasks.definition.TaskCollectItems;
import me.dmillerw.minions.util.Area;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class ItemDebug extends Item {

    public ItemDebug() {
        super();

        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (!playerIn.world.isRemote && target instanceof EntityMinion) {
            ParameterMap map = new ParameterMap();
            map.setParameterValue(TaskCollectItems.HARVEST_AREA, new Area(new BlockPos(0, 5, 0), new BlockPos(16, 16, 16)));
            map.setParameterValue(TaskCollectItems.DROPOFF_POINT, new BlockPos(18, 5, 18));

            TaskInstance instance = TaskRegistry.getTask(TaskRegistry.COLLECT_ITEMS).createInstance(UUID.randomUUID(), map);

            ((EntityMinion) target).taskInstance = instance;
        }

        return target instanceof EntityMinion;
    }
}
