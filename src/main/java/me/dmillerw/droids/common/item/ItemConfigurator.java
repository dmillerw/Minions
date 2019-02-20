package me.dmillerw.droids.common.item;

import me.dmillerw.droids.common.ModInfo;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemConfigurator extends Item {

    public static final String NAME = "configurator";

    public ItemConfigurator() {
        super();

        setMaxStackSize(1);
        setRegistryName(new ResourceLocation(ModInfo.ID, NAME));
        setTranslationKey(NAME);
    }
}
