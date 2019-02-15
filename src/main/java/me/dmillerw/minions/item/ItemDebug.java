package me.dmillerw.minions.item;

import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ItemDebug extends Item {

    public ItemDebug() {
        super();

        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (!playerIn.world.isRemote && target instanceof EntityMinion) {
//            ParameterMap map = new ParameterMap();
//            map.setParameterValue(TaskCollectItems.HARVEST_AREA, new Area(new BlockPos(0, 5, 0), new BlockPos(16, 16, 16)));
//            map.setParameterValue(TaskCollectItems.DROPOFF_POINT, new BlockPos(18, 5, 18));
//
//            TaskInstance instance = TaskRegistry.getTask(TaskRegistry.COLLECT_ITEMS).createInstance(UUID.randomUUID(), map);
//
//            ((EntityMinion) target).taskInstance = instance;
        }

        return target instanceof EntityMinion;
    }
}
