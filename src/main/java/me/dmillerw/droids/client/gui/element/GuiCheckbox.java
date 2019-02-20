package me.dmillerw.droids.client.gui.element;

import me.dmillerw.droids.client.gui.GuiBase;

import java.util.function.Consumer;

public class GuiCheckbox extends GuiElement<GuiCheckbox> {

    private Consumer<GuiCheckbox> clickCallback;

    public String label;
    public int labelColor;
    public boolean isChecked;

    public GuiCheckbox(GuiBase parentGui, int width, int height) {
        super(parentGui, width, height);
    }

    public GuiCheckbox(GuiBase parentGui, int x, int y, int width, int height, String label, int color) {
        this(parentGui, x, y, width, height, label, color, false);
    }

    public GuiCheckbox(GuiBase parentGui, int x, int y, int width, int height, String label, int color, boolean isChecked) {
        super(parentGui, x, y, width, height);

        this.label = label;
        this.labelColor = color;
        this.isChecked = isChecked;
    }

    public GuiCheckbox onClick(Consumer<GuiCheckbox> clickCallback) {
        this.clickCallback = clickCallback;
        return this;
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        super.drawElement(mouseX, mouseY);
        drawTexturedElement();

        mc.fontRenderer.drawString(label, xPos + width + 2, yPos + height / 2 - 4, labelColor);
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int mouseButton) {
        boolean inside = super.onMouseClick(mouseX, mouseY, mouseButton);
        if (inside && clickCallback != null)
            clickCallback.accept(this);
        return inside;
    }
}
