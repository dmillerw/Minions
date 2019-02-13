package me.dmillerw.minions.client.render;

import me.dmillerw.minions.entity.EntityMinion;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class RenderMinion extends RenderLivingBase<EntityMinion> {

    public RenderMinion(RenderManager renderManager) {
        super(renderManager, new ModelBiped(0.0F, 0.0F, 64, 64), 0.5F);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityMinion entity) {
        return DefaultPlayerSkin.getDefaultSkinLegacy();
    }
}
