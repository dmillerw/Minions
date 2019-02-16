package me.dmillerw.minions.client.gui.element;

import me.dmillerw.minions.client.gui.GuiBase;

public class GuiTextLabel extends GuiElement<GuiTextLabel> {

    public static enum Alignment {

        TOP_LEFT, TOP_CENTER, TOP_RIGHT, CENTER_LEFT, CENTER, CENTER_RIGHT, BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT
    }

    public Alignment alignment = Alignment.TOP_LEFT;

    public String label;
    public int labelColor;

    public boolean trimToFit = false;

    public GuiTextLabel(GuiBase parentGui, int width, int height) {
        super(parentGui, width, height);
    }

    public GuiTextLabel(GuiBase parentGui, int xPos, int yPos, int width, int height) {
        super(parentGui, xPos, yPos, width, height);
    }

    public GuiTextLabel setTextAlignment(Alignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public GuiTextLabel setLabel(String label) {
        return setLabel(label, 0xFFFFFFFF);
    }

    public GuiTextLabel setLabel(String label, int color) {
        this.label = label;
        this.labelColor = color;
        return this;
    }

    public GuiTextLabel setTrimToFit(boolean trimToFit) {
        this.trimToFit = trimToFit;
        return this;
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        super.drawElement(mouseX, mouseY);

        String tempLabel = trimToFit ? mc.fontRenderer.trimStringToWidth(label, width) : label;

        int sx = xPos;
        int sy = yPos;
        int ex = xPos + width;
        int ey = yPos + height;

        int labelHeight = mc.fontRenderer.FONT_HEIGHT;
        int labelWidth = mc.fontRenderer.getStringWidth(tempLabel);

        int drawX = sx;
        int drawY = sy;

        switch (alignment) {
            case TOP_LEFT:
            default:
                break;

            case TOP_CENTER:
                drawX = sx + width / 2 - labelWidth / 2;
                break;

            case TOP_RIGHT:
                drawX = ex - labelWidth;
                break;

            case CENTER_LEFT:
                drawY = sy + height / 2 - labelHeight / 2;
                break;

            case CENTER:
                drawX = sx + width / 2 - labelWidth / 2;
                drawY = sy + height / 2 - labelHeight / 2;
                break;

            case CENTER_RIGHT:
                drawX = ex - labelWidth;
                drawY = sy + height / 2 - labelHeight / 2;
                break;

            case BOTTOM_LEFT:
                drawY = ex - labelHeight;
                break;

            case BOTTOM_CENTER:
                drawX = sx + width / 2 - labelWidth / 2;
                drawY = ex - labelHeight;
                break;

            case BOTTOM_RIGHT:
                drawX = ex - labelWidth;
                drawY = ex - labelHeight;
        }

        mc.fontRenderer.drawString(tempLabel, drawX, drawY, labelColor);
    }
}
