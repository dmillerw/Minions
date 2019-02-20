package me.dmillerw.droids.client.gui.element;

import me.dmillerw.droids.client.gui.GuiBase;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public abstract class GuiWrappedElement<V extends Gui> extends GuiElement<GuiWrappedElement> {

    protected V element;

    public GuiWrappedElement(GuiBase parentGui, int width, int height) {
        super(parentGui, width, height);
    }

    public GuiWrappedElement(GuiBase parentGui, int xPos, int yPos, int width, int height) {
        super(parentGui, xPos, yPos, width, height);
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        if (!visible) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(xPos, yPos, parentGui.getZLevel());

        renderVanillaElement(mouseX, mouseY);

        GlStateManager.popMatrix();
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (super.onMouseClick(mouseX, mouseY, mouseButton)) {
            return handleVanllaMouseClick(mouseX, mouseY, mouseButton);
        }

        return false;
    }

    @Override
    public boolean onKeyTyped(char character, int keycode) {
        if (active && visible) {
            return handleVanillaKeyPress(character, keycode);
        }

        return false;
    }

    public abstract void renderVanillaElement(int mouseX, int mouseY);
    public abstract boolean handleVanllaMouseClick(int mouseX, int mouseY, int mouseButton);
    public abstract boolean handleVanillaKeyPress(char character, int keycode);
}
