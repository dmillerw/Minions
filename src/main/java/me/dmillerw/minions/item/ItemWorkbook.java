package me.dmillerw.minions.item;

import me.dmillerw.minions.client.gui.GuiJobList;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemWorkbook extends Item {

    public ItemWorkbook() {
        super();

        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack held = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiJobList());
        }
        return ActionResult.newResult(EnumActionResult.SUCCESS, held);
    }
}
