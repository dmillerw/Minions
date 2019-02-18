package me.dmillerw.minions.client.gui;

import com.google.common.collect.Maps;
import me.dmillerw.minions.client.gui.element.GuiElement;
import me.dmillerw.minions.client.gui.element.GuiWrappedTextField;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Map;

public class GuiBase extends GuiScreen {

    private ResourceLocation texture;

    private Map<String, GuiElement> elements = Maps.newHashMap();

    protected int xSize;
    protected int ySize;

    protected int guiLeft;
    protected int guiTop;

    protected boolean doesGuiPauseGame = false;

    public GuiBase(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;

        elements.clear();
    }

    @Override
    public final void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        final float delta = Mouse.getDWheel();
        if (delta != 0) {
            onMouseScroll(mouseX, mouseY, delta);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);

        drawBackground(mouseX, mouseY);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture);

        drawForeground(mouseX, mouseY);

        this.mc.getTextureManager().bindTexture(texture);
        elements.values().forEach((element -> element.drawElement(mouseX, mouseY)));
        elements.values().forEach((element -> element.drawTooltip(mouseX, mouseY)));
    }

    protected void drawBackground(int mouseX, int mouseY) {
        this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    protected void drawForeground(int mouseX, int mouseY) {
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (GuiElement element : elements.values()) {
            if (element.onMouseClick(mouseX, mouseY, mouseButton)) {
                // FIXME
                elements.values().stream()
                        .filter((e) -> e instanceof GuiWrappedTextField)
                        .forEach((e) -> ((GuiWrappedTextField) e).setFocused(false));

                if (element instanceof GuiWrappedTextField) {
                    ((GuiWrappedTextField) element).setFocused(true);
                }

                return;
            }
        }
    }

    protected void onMouseScroll(int mouseX, int mouseY, float delta) {}

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        for (GuiElement element : elements.values()) {
            if (element.onKeyTyped(typedChar, keyCode))
                return;
        }
    }

    /* CALLBACKS */

    public void onGuiPaused() {}

    /* UTIL */

    protected boolean setGuiDimensions(int xSize, int ySize) {
        if (xSize != this.xSize || ySize != this.ySize) {
            this.xSize = xSize;
            this.ySize = ySize;
            this.initGui();

            return true;
        }

        return false;
    }

    protected void clearElements() {
        this.elements.clear();
    }

    protected void addElement(String key, GuiElement element) {
        this.elements.put(key, element);
    }

    public <T extends GuiElement> T getElement(String key) {
        return (T) this.elements.get(key);
    }

    protected void playClickSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    public final void bindTexture() {
        mc.getTextureManager().bindTexture(texture);
    }

    public float getZLevel() {
        return zLevel;
    }

    public final int getGuiLeft() {
        return guiLeft;
    }

    public final int getGuiTop() {
        return guiTop;
    }
}
