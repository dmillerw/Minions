package me.dmillerw.minions.client.gui.element;

import me.dmillerw.minions.client.gui.GuiBase;
import net.minecraft.client.gui.GuiTextField;

import java.util.function.Consumer;

public class GuiWrappedTextField extends GuiWrappedElement<GuiTextField> {

    private Consumer<String> callback;

    public GuiWrappedTextField(GuiBase parentGui, int width, int height) {
        super(parentGui, width, height);

        this.element = new GuiTextField(0, mc.fontRenderer, 0, 0, width, height);
    }

    public GuiWrappedTextField(GuiBase parentGui, int xPos, int yPos, int width, int height) {
        super(parentGui, xPos, yPos, width, height);

        this.element = new GuiTextField(0, mc.fontRenderer, 0, 0, width, height);
    }

    public GuiWrappedTextField onTextChange(Consumer<String> callback) {
        this.callback = callback;
        return this;
    }

    public GuiWrappedTextField setDrawBackground(boolean dropBackground) {
        element.setEnableBackgroundDrawing(dropBackground);
        return this;
    }

    public GuiWrappedTextField setFocused(boolean focused) {
        element.setFocused(focused);
        return this;
    }

    public String getText() {
        return element.getText();
    }

    public GuiWrappedTextField setText(String text) {
        element.setText(text);
        return this;
    }

    public GuiWrappedTextField setCursorPosition(int position) {
        element.setCursorPosition(position);
        return this;
    }

    @Override
    public void renderVanillaElement(int mouseX, int mouseY) {
        element.drawTextBox();
    }

    @Override
    public boolean handleVanllaMouseClick(int mouseX, int mouseY, int mouseButton) {
        return element.mouseClicked(mouseX - xPos, mouseY - yPos, mouseButton);
    }

    @Override
    public boolean handleVanillaKeyPress(char character, int keycode) {
        boolean input = element.textboxKeyTyped(character, keycode);
        if (input && callback != null) callback.accept(element.getText().trim());
        return input;
    }
}
