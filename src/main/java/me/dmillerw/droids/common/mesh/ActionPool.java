package me.dmillerw.droids.common.mesh;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class ActionPool extends WorldSavedData {

    public static final String KEY = "action_pool";

    public ActionPool() {
        super(KEY);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return null;
    }
}
