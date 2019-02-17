package me.dmillerw.minions.client.gui.element;

import me.dmillerw.minions.client.gui.GuiBase;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.function.Consumer;

public class GuiWrappedButton extends GuiWrappedElement<GuiButtonExt> {

    private Consumer<GuiWrappedButton> clickCallback;

    public GuiWrappedButton(GuiBase parentGui, int width, int height, String displayString) {
        super(parentGui, width, height);

        this.element = new GuiButtonExt(0, 0, 0, width, height, displayString);
    }

    public GuiWrappedButton(GuiBase parentGui, int xPos, int yPos, int width, int height, String displayString) {
        super(parentGui, xPos, yPos, width, height);

        this.element = new GuiButtonExt(0, 0, 0, width, height, displayString);
    }

    public GuiWrappedButton onClick(Consumer<GuiWrappedButton> clickCallback) {
        this.clickCallback = clickCallback;
        return this;
    }

    @Override
    public void renderVanillaElement(int mouseX, int mouseY) {
        element.drawButton(mc, mouseX - xPos, mouseY - yPos, 0);
    }

    @Override
    public boolean handleVanllaMouseClick(int mouseX, int mouseY, int mouseButton) {
        boolean pressed = element.mousePressed(mc, mouseX - xPos, mouseY - yPos);
        if (pressed && clickCallback != null) clickCallback.accept(this);
        return pressed;
    }

    @Override
    public boolean handleVanillaKeyPress(char character, int keycode) {
        return false;
    }
}
