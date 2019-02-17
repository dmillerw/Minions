package me.dmillerw.minions.item;

import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
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
            if (playerIn.isSneaking()) {
                ((EntityMinion) target).activeTaskStep = null;
                target.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
            }
        }

        return target instanceof EntityMinion;
    }
}
