package me.dmillerw.minions.entity;

import me.dmillerw.minions.entity.ai.EntityAIExecuteTask;
import me.dmillerw.minions.tasks.TaskInstance;
import net.minecraft.entity.EntityLiving;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class EntityMinion extends EntityLiving {

    private static final List<ItemStack> ARMOUR = new ArrayList<>();

    private static final DataParameter<String> SKIN = EntityDataManager.createKey(EntityMinion.class, DataSerializers.STRING);
    private static final DataParameter<ItemStack> HELDITEM = EntityDataManager.createKey(EntityMinion.class, DataSerializers.ITEM_STACK);

    public TaskInstance taskInstance;

    public EntityMinion(World worldIn) {
        super(worldIn);
    }

    @Override
    protected void entityInit() {
        super.entityInit();

        dataManager.register(SKIN, "");
        dataManager.register(HELDITEM, ItemStack.EMPTY);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();

        this.tasks.addTask(1, new EntityAIExecuteTask(this, 1.2));
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
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
        return slotIn == EntityEquipmentSlot.MAINHAND ? dataManager.get(HELDITEM) : ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack) {
        if (slotIn == EntityEquipmentSlot.MAINHAND)
            dataManager.set(HELDITEM, stack.copy());
    }

    @Override
    public EnumHandSide getPrimaryHand() {
        return EnumHandSide.RIGHT;
    }
}
