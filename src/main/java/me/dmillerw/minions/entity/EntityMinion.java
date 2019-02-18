package me.dmillerw.minions.entity;

import me.dmillerw.minions.client.gui.GuiMinion;
import me.dmillerw.minions.entity.ai.AIHelper;
import me.dmillerw.minions.entity.ai.EntityAIExecuteTask;
import me.dmillerw.minions.tasks.TaskStep;
import me.dmillerw.minions.util.MinionType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

public class EntityMinion extends EntityLiving {

    private static final List<ItemStack> ARMOUR = new ArrayList<>();

    private static final DataParameter<String> SKIN = EntityDataManager.createKey(EntityMinion.class, DataSerializers.STRING);
    private static final DataParameter<ItemStack> HELD_ITEM = EntityDataManager.createKey(EntityMinion.class, DataSerializers.ITEM_STACK);
    private static final DataParameter<String> TYPE = EntityDataManager.createKey(EntityMinion.class, DataSerializers.STRING);

    public AIHelper aiHelper;

    public TaskStep activeTaskStep;

    public EntityMinion(World worldIn) {
        super(worldIn);

        this.aiHelper = new AIHelper(this, 0.45F);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(SKIN, "");
        dataManager.register(HELD_ITEM, ItemStack.EMPTY);
        dataManager.register(TYPE, MinionType.LABORER.name());
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        this.tasks.addTask(1, new EntityAIExecuteTask(this, 0.45));
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();

//        if (!world.isRemote) {
//            if (activeTaskStep == null) {
//                WorldJobData data = WorldJobData.getJobBoard(world);
//                if (!data.getJobs().isEmpty()) {
//                    Job job = data.getJobs().get(0);
//                    activeTaskStep = job.getStep();
//                    if (activeTaskStep != null) {
//                        activeTaskStep.setClaimed(true);
//                    }
//                }
//            }
//        }
    }

    public ItemStack getHeldItem() {
        return getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
    }

    public String getSkin() {
        return dataManager.get(SKIN);
    }

    public void setSkin(String skin) {
        dataManager.set(SKIN, skin);
    }

    public MinionType getMinionType() {
        return MinionType.fromString(dataManager.get(TYPE));
    }

    public void setMinionType(MinionType type) {
        dataManager.set(TYPE, type.getString());
    }

    @SideOnly(Side.CLIENT)
    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (player.getHeldItem(hand).isEmpty()) {
            if (player.world.isRemote) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiMinion(this));
            }
        }

        return player.getHeldItem(hand).isEmpty();
    }

    @Override
    public void setCustomNameTag(String name) {
        setSkin(name);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);

        compound.setString("skin", getSkin());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("skin"))
            setSkin(compound.getString("skin"));
        else
            setSkin("");
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return ARMOUR;
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
        return slotIn == EntityEquipmentSlot.MAINHAND ? dataManager.get(HELD_ITEM) : ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        if (slotIn == EntityEquipmentSlot.MAINHAND)
            dataManager.set(HELD_ITEM, stack.copy());
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return EnumHandSide.RIGHT;
    }
}
