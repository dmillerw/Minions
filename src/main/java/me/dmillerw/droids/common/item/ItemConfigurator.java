package me.dmillerw.droids.common.item;

import me.dmillerw.droids.api.INetworkComponent;
import me.dmillerw.droids.api.IPlayerOwned;
import me.dmillerw.droids.common.ModInfo;
import me.dmillerw.droids.common.entity.EntityDroid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemConfigurator extends Item {

    public static final String NAME = "configurator";

    public static boolean debugPauseDroidTicks = false;

    public ItemConfigurator() {
        super();

        setMaxStackSize(1);
        setRegistryName(new ResourceLocation(ModInfo.ID, NAME));
        setTranslationKey(NAME);

        setCreativeTab(ModInfo.TAB);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (playerIn.world.isRemote)
            return true;

        if (playerIn.isSneaking()) {
            if (!(target instanceof EntityDroid))
                return false;

            playerIn.sendMessage(new TextComponentString("DROID ID: " + target.getUniqueID()));
            playerIn.sendMessage(new TextComponentString("SKIN: " + ((EntityDroid) target).getSkin()));
            playerIn.sendMessage(new TextComponentString("ACTION: " + ((EntityDroid) target).getAction()));

            return true;
        }
        return false;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote)
            return EnumActionResult.FAIL;

        if (player.isSneaking()) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof IPlayerOwned) {
                player.sendMessage(new TextComponentString("OWNER: " + ((IPlayerOwned) tile).getOwner()));
            }
            if (tile instanceof INetworkComponent) {
                player.sendMessage(new TextComponentString("RANGE: " + ((INetworkComponent) tile).getRange()));
                player.sendMessage(new TextComponentString("NETWORK: " + ((INetworkComponent) tile).getNetwork()));
            }

            player.sendMessage(new TextComponentString(""));

            return EnumActionResult.SUCCESS;
        } else {
            debugPauseDroidTicks = !debugPauseDroidTicks;
            player.sendMessage(new TextComponentString("TICK PAUSE: " + debugPauseDroidTicks));
        }

        return EnumActionResult.FAIL;
    }
}
