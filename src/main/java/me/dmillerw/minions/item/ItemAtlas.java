package me.dmillerw.minions.item;

import me.dmillerw.minions.client.gui.Navigation;
import me.dmillerw.minions.client.gui.modal.GuiModalYesNo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemAtlas extends Item {

    public ItemAtlas() {
        super();

        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.MISC);

        setTranslationKey("atlas");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if (worldIn.isRemote) {
            Navigation.INSTANCE.push(new GuiModalYesNo("Delete Job?", "Are you sure you want to delete this job? Once deleted, it cannot be recovered!", "No", "Yes"), System.out::println);
        }

        return ActionResult.newResult(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
    }
}
