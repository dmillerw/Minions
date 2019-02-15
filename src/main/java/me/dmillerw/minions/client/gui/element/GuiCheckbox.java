package me.dmillerw.minions.client.gui.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiCheckbox extends Gui {

    public int x;
    public int y;
    public int width;
    public int height;
    public String label;
    public int labelColor;
    public boolean isChecked;

    private ResourceLocation resourceLocation;

    private int inactiveU;
    private int inactiveV;
    private int activeU;
    private int activeV;

    public GuiCheckbox(int x, int y, int width, int height, String label, int color) {
        this(x, y, width, height,label, color, false);
    }

    public GuiCheckbox(int x, int y, int width, int height, String label, int color, boolean isChecked) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;
        this.labelColor = color;
        this.isChecked = isChecked;
    }

    public GuiCheckbox setResourceLocation(ResourceLocation resourceLocation) {
        this.resourceLocation = resourceLocation;
        return this;
    }

    public GuiCheckbox setUV(int inactiveU, int inactiveV, int activeU, int activeV) {
        this.inactiveU = inactiveU;
        this.inactiveV = inactiveV;
        this.activeU = activeU;
        this.activeV = activeV;
        return this;
    }

    public void drawCheckbox(Minecraft mc, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(resourceLocation);
        GlStateManager.color(1, 1, 1, 1);
        drawTexturedModalRect(x, y, isChecked ? activeU : inactiveU, isChecked ? activeV : inactiveV, width, height);
        mc.fontRenderer.drawString(label, x + width + 2, y + width / 2 - mc.fontRenderer.FONT_HEIGHT / 2, labelColor);
    }

    public boolean onMouseClick(int mouseX, int mouseY, int mouseButton) {
        boolean mousein = (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + width);
        if (mousein)
            isChecked = !isChecked;
        return mousein;
    }
}
