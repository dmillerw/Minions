package me.dmillerw.minions.item;

import me.dmillerw.minions.client.handler.CoordSelectionHandler;
import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemDebug extends Item {

    public ItemDebug() {
        super();

        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MISC);

        setTranslationKey("debug");
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            if (CoordSelectionHandler.INSTANCE.isSelecting()) {
                CoordSelectionHandler.INSTANCE.selectCoordinate(pos);
            }
        }
        return EnumActionResult.PASS;
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
