package me.dmillerw.minions.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class GuiTexturedButton extends Gui {

    public int x;
    public int y;
    public int width;
    public int height;

    private int disabledU;
    private int disabledV;
    private int inactiveU;
    private int inactiveV;
    private int activeU;
    private int activeV;

    private boolean disabled = false;

    public GuiTexturedButton(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public GuiTexturedButton setUV(int u, int v) {
        setDisabledUV(u, v);
        setInactiveUV(u, v);
        setActiveUV(u, v);
        return this;
    }

    public GuiTexturedButton setDisabledUV(int u, int v) {
        this.disabledU = u;
        this.disabledV = v;
        return this;
    }

    public GuiTexturedButton setInactiveUV(int u, int v) {
        this.inactiveU = u;
        this.inactiveV = v;
        return this;
    }

    public GuiTexturedButton setActiveUV(int u, int v) {
        this.activeU = u;
        this.activeV = v;
        return this;
    }

    public GuiTexturedButton setActive(boolean active) {
        this.disabled = !active;
        return this;
    }

    public boolean mousePressed(int mouseX, int mouseY) {
        return !disabled && mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
        int u = disabled ? disabledU : hovered ? activeU : inactiveU;
        int v = disabled ? disabledV : hovered ? activeV : inactiveV;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.drawTexturedModalRect(x, y, u, v, width, height);
    }
}
