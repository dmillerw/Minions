package me.dmillerw.droids.client.gui.element;

import me.dmillerw.droids.client.gui.GuiBase;
import me.dmillerw.droids.client.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.apache.commons.lang3.tuple.Pair;

import java.util.function.Function;

public class GuiElement<T extends GuiElement> extends Gui {

    protected final GuiBase parentGui;
    protected final Minecraft mc;

    private Function<T, Pair<Integer, Integer>> uvMappingFunction = (element) -> Pair.of(0, 0);

    protected int xPos;
    protected int yPos;
    protected int width;
    protected int height;

    public boolean active = true;
    public boolean visible = true;
    public String tooltip;

    public GuiElement(GuiBase parentGui, int width, int height) {
        this.parentGui = parentGui;
        this.mc = parentGui.mc;
        this.width = width;
        this.height = height;
    }

    public GuiElement(GuiBase parentGui, int xPos, int yPos, int width, int height) {
        this.parentGui = parentGui;
        this.mc = parentGui.mc;
        this.xPos = parentGui.getGuiLeft() + xPos;
        this.yPos = parentGui.getGuiTop() + yPos;
        this.width = width;
        this.height = height;
    }

    public T setPosition(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        return (T) this;
    }

    public T setUVMapper(Function<T, Pair<Integer, Integer>> uvMappingFunction) {
        this.uvMappingFunction = uvMappingFunction;
        return (T) this;
    }

    public T setActive(boolean active) {
        this.active = active;
        return (T) this;
    }

    public T setVisible(boolean visible) {
        this.visible = visible;
        return (T) this;
    }

    public T setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return (T) this;
    }

    public void drawElement(int mouseX, int mouseY) {
        if (!visible)
            return;

        parentGui.bindTexture();
    }

    public final void drawTooltip(int mouseX, int mouseY) {
        if (active) {
            if (tooltip != null && !tooltip.isEmpty()) {
                if (mouseInBounds(mouseX, mouseY)) {
                    RenderUtils.drawTooltip(parentGui, mc.fontRenderer, mouseX, mouseY, tooltip);
                }
            }
        }
    }

    public boolean onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (!visible || !active)
            return false;

        return mouseInBounds(mouseX, mouseY);
    }

    public boolean onKeyTyped(char character, int keycode) {
        if (!visible || !active)
            return false;

        return false;
    }

    /* DRAW UTILS */

    protected void drawTexturedElement() {
        GlStateManager.color(1, 1, 1, 1);

        Pair<Integer, Integer> uv = getUV();
        drawTexturedModalRect(xPos, yPos, uv.getKey(), uv.getValue(), width, height);
    }

    /* UTIL */

    public Pair<Integer, Integer> getUV() {
        return uvMappingFunction.apply((T)this);
    }

    protected boolean mouseInBounds(int mouseX, int mouseY) {
        return mouseInBounds(mouseX, mouseY, xPos, yPos, width, height);
    }

    protected boolean mouseInBounds(int mouseX, int mouseY, int xPos, int yPos, int width, int height) {
        return mouseX >= xPos && mouseX <= xPos + width && mouseY >= yPos && mouseY <= yPos + height;
    }
}
