package me.dmillerw.droids.client.gui.element;

import me.dmillerw.droids.client.gui.GuiBase;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

public class GuiTexturedButton extends GuiElement<GuiTexturedButton> {

    private Consumer<GuiTexturedButton> clickCallback;
    public boolean hovered;

    public GuiTexturedButton(GuiBase parentGui, int width, int height) {
        super(parentGui, width, height);
    }

    public GuiTexturedButton(GuiBase parentGui, int xPos, int yPos, int width, int height) {
        super(parentGui, xPos, yPos, width, height);
    }

    public GuiTexturedButton onClick(Consumer<GuiTexturedButton> clickCallback) {
        this.clickCallback = clickCallback;
        return this;
    }

    @Override
    public void drawElement(int mouseX, int mouseY) {
        super.drawElement(mouseX, mouseY);

        this.hovered = mouseInBounds(mouseX, mouseY);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        drawTexturedElement();
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int mouseButton) {
        boolean inside = super.onMouseClick(mouseX, mouseY, mouseButton);
        if (inside && clickCallback != null) {
            clickCallback.accept(this);
        }
        return inside;
    }
}
