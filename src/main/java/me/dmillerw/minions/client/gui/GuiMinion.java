package me.dmillerw.minions.client.gui;

import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.network.server.SSetMinionName;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiMinion extends GuiScreen {

    public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation(ModInfo.ID, "textures/gui/minion.png");

    private static final int X_SIZE = 176;
    private static final int Y_SIZE = 166;

    private int guiLeft;
    private int guiTop;

    private float oldMouseX;
    private float oldMouseY;

    private EntityMinion minion;

    private GuiTextField nameField;

    public GuiMinion(EntityMinion minion) {
        this.minion = minion;
    }

    public void initGui() {
        super.initGui();

        Keyboard.enableRepeatEvents(true);

        this.guiLeft = (this.width - X_SIZE) / 2;
        this.guiTop = (this.height - Y_SIZE) / 2;

        this.nameField = new GuiTextField(0, fontRenderer, guiLeft + 63, guiTop +20, 105, 20);
        this.nameField.setFocused(true);
        this.nameField.setText(this.minion.getSkin());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.drawTexturedModalRect(i, j, 0, 0, X_SIZE, Y_SIZE);

        GuiInventory.drawEntityOnScreen(i + 32, j + 45, 30, (float)(i + 51) - this.oldMouseX, (float)(j + 75 - 50) - this.oldMouseY, this.minion);

        this.oldMouseX = (float)mouseX;
        this.oldMouseY = (float)mouseY;

        fontRenderer.drawString("Name:", guiLeft + 63, guiTop + 8, 0);
        nameField.drawTextBox();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (nameField.mouseClicked(mouseX, mouseY, mouseButton))
            return;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (keyCode == Keyboard.KEY_RETURN) {
            if (nameField.isFocused()) {
                updateMinionName();
                return;
            }
        }

        if (nameField.textboxKeyTyped(typedChar, keyCode))
            return;
    }

    private void updateMinionName() {
        SSetMinionName packet = new SSetMinionName();
        packet.targetUuid = minion.getUniqueID();
        packet.name = nameField.getText();

        PacketHandler.INSTANCE.sendToServer(packet);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);

        updateMinionName();
    }
}
