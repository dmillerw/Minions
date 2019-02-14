package me.dmillerw.minions.client.render;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import org.codehaus.plexus.util.StringUtils;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class RenderMinion extends RenderLivingBase<EntityMinion> {

    private static Map<String, ResourceLocation> skinMap = Maps.newHashMap();

    private static ResourceLocation getSkin(String name) {
        if (!skinMap.containsKey("DEFAULT")) skinMap.put("DEFAULT", DefaultPlayerSkin.getDefaultSkin(UUID.randomUUID()));

        if (StringUtils.isBlank(name)) name = "DEFAULT";

        ResourceLocation resourceLocation = skinMap.get(name);
        if (resourceLocation == null) {
            GameProfile profile = TileEntitySkull.updateGameProfile(new GameProfile(null, name));
            Minecraft minecraft = Minecraft.getMinecraft();
            Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().loadSkinFromCache(profile);

            if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                resourceLocation = minecraft.getSkinManager().loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
            } else {
                UUID uuid = EntityPlayer.getUUID(profile);
                resourceLocation = DefaultPlayerSkin.getDefaultSkin(uuid);
            }

            skinMap.put(name, resourceLocation);
        }

        return resourceLocation;
    }

    public RenderMinion(RenderManager renderManager) {
        super(renderManager, new ModelMinion(), 0.5F);
    }

    @Override
    public void doRender(EntityMinion entity, double x, double y, double z, float entityYaw, float partialTicks) {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

        final ItemStack held = entity.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
//        final ItemStack held = new ItemStack(Items.BUCKET);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.rotate(180.0F - this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks), 0, 1, 0);
        GlStateManager.translate(0, 0.65, -0.4);
        Minecraft.getMinecraft().getRenderItem().renderItem(held, ItemCameraTransforms.TransformType.GROUND);

        GlStateManager.popMatrix();
    }

    @Override
    protected boolean canRenderName(EntityMinion entity) {
        return false;
    }

    @Override
    public void renderName(EntityMinion entity, double x, double y, double z) {

    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityMinion entity) {
        return getSkin(entity.getSkin());
    }
}
