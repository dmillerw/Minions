package me.dmillerw.minions.client.gui;

import com.google.common.collect.Lists;
import me.dmillerw.minions.client.gui.element.GuiCheckbox;
import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.tasks.Job;
import me.dmillerw.minions.tasks.ParameterMap;
import me.dmillerw.minions.tasks.TaskDefinition;
import me.dmillerw.minions.util.MinionType;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class GuiModifyJob extends GuiScreen {

    private static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation(ModInfo.ID, "textures/gui/modify_job.png");

    private static final int FONT_COLOR = 0xff544c3b;
    
    private static final int X_SIZE = 256;
    private static final int Y_SIZE = 204;

    private static final int SEARCH_X = 29;
    private static final int SEARCH_Y = 5;

    private static final int LIST_X = 15;
    private static final int LIST_Y = 18;
    private static final int LIST_W = 80;
    private static final int LIST_H = 182;

    private static final int BOX_WIDTH = 80;
    private static final int BOX_HEIGHT = 13;
    private static final int BOX_TEXT_X = 2;
    private static final int BOX_U = 0;
    private static final int BOX_V = 214;

    private static final int BOX_PER_PAGE = LIST_H / BOX_HEIGHT;

    private static final int BUTTON_OK_X = 142;
    private static final int BUTTON_OK_Y = 203;
    private static final int BUTTON_OK_W = 11;
    private static final int BUTTON_OK_H = 10;
    private static final int BUTTON_OK_U = 7;
    private static final int BUTTON_OK_V = 216;

    private static final int BUTTON_CANCEL_X = 128;
    private static final int BUTTON_CANCEL_Y = 203;
    private static final int BUTTON_CANCEL_W = 11;
    private static final int BUTTON_CANCEL_H = 10;
    private static final int BUTTON_CANCEL_U = 18;
    private static final int BUTTON_CANCEL_V = 216;

    private int guiLeft;
    private int guiTop;

    private GuiTextField nameField;

    private GuiCheckbox checkboxAnyone;
    private GuiCheckbox checkboxLaborer;
    private GuiCheckbox checkboxFarmer;
    private GuiCheckbox checkboxBuilder;
    private GuiCheckbox checkboxForester;

    private Job job;

    private int paramsScrollIndex = 0;
    private float paramsScrollProgress = 0;
    private int selectedParamIndex = -1;

    private List<String> parameters = Lists.newArrayList();

    public GuiModifyJob(TaskDefinition task) {
        this(new Job(UUID.randomUUID(), task.name, task, MinionType.ANYONE, 1, new ParameterMap(task)));
    }

    public GuiModifyJob(Job job) {
        this.job = job;

        parameters.addAll(job.parameters.getKeys());
    }

    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - X_SIZE) / 2;
        this.guiTop = (this.height - Y_SIZE) / 2;

        this.nameField = new GuiTextField(0, fontRenderer, guiLeft + SEARCH_X, guiTop + SEARCH_Y, 100, 22);
        this.nameField.setFocused(true);
        this.nameField.setEnableBackgroundDrawing(false);

        //TODO: Better gui widgets, seriously
        this.checkboxAnyone = new GuiCheckbox(guiLeft + 99, guiTop + 159, 11, 10, "Anyone", FONT_COLOR, false)
                .setResourceLocation(INVENTORY_BACKGROUND)
                .setUV(29, 204, 40, 204);
        this.checkboxLaborer = new GuiCheckbox(guiLeft + 99, guiTop + 174, 11, 10, "Laborer", FONT_COLOR, false)
                .setResourceLocation(INVENTORY_BACKGROUND)
                .setUV(29, 204, 40, 204);
        this.checkboxFarmer = new GuiCheckbox(guiLeft + 99, guiTop + 189, 11, 10, "Farmer", FONT_COLOR, false)
                .setResourceLocation(INVENTORY_BACKGROUND)
                .setUV(29, 204, 40, 204);
        this.checkboxBuilder = new GuiCheckbox(guiLeft + 159, guiTop + 159, 11, 10, "Builder", FONT_COLOR, false)
                .setResourceLocation(INVENTORY_BACKGROUND)
                .setUV(29, 204, 40, 204);
        this.checkboxForester = new GuiCheckbox(guiLeft + 159, guiTop + 174, 11, 10, "Forester", FONT_COLOR, false)
                .setResourceLocation(INVENTORY_BACKGROUND)
                .setUV(29, 204, 40, 204);

        if (job != null) {
            nameField.setText(job.title);
            nameField.setCursorPosition(0);

            setMinionType(job.type);
        } else {
            setMinionType(MinionType.ANYONE);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scroll();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int x = this.guiLeft;
        int y = this.guiTop;
        this.drawTexturedModalRect(x, y, 0, 0, X_SIZE, Y_SIZE);

        drawTexturedModalRect(x + BUTTON_OK_X, y + BUTTON_OK_Y, BUTTON_OK_U, BUTTON_OK_V, BUTTON_OK_W, BUTTON_OK_H);
        drawTexturedModalRect(x + BUTTON_CANCEL_X, y + BUTTON_CANCEL_Y, BUTTON_CANCEL_U, BUTTON_CANCEL_V, BUTTON_CANCEL_W, BUTTON_CANCEL_H);

        for (int i=0; i<BOX_PER_PAGE; i++) {
            mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);

            GlStateManager.color(1, 1, 1,1 );

            int index = paramsScrollIndex + i;
            if (index >= parameters.size()) break;

            int bx = x + LIST_X;
            int by = y + LIST_Y + BOX_HEIGHT * i;
            String parameter = parameters.get(index);
            
            if (index == selectedParamIndex) {
                GlStateManager.color(1, 0, 0, 1);
                drawTexturedModalRect(bx, by, BOX_U, BOX_V, BOX_WIDTH, BOX_HEIGHT);
                fontRenderer.drawString(parameter, bx + 5, by + 3, 0xFFFFFFFF);
            } else {
                drawTexturedModalRect(bx, by, BOX_U, BOX_V, BOX_WIDTH, BOX_HEIGHT);
                fontRenderer.drawString(parameter, bx + 5, by + 3, FONT_COLOR);
            }
        }
        
        fontRenderer.drawString("Title", x + 4, y + 5, FONT_COLOR);

        nameField.drawTextBox();

        checkboxAnyone.drawCheckbox(mc, mouseX, mouseY);
        checkboxLaborer.drawCheckbox(mc, mouseX, mouseY);
        checkboxFarmer.drawCheckbox(mc, mouseX, mouseY);
        checkboxBuilder.drawCheckbox(mc, mouseX, mouseY);
        checkboxForester.drawCheckbox(mc, mouseX, mouseY);
    }

    private void setMinionType(MinionType type) {
        clearCheckboxes();
        switch (type) {
            case LABORER:
                checkboxLaborer.isChecked = true;
                break;
            case FARMER:
                checkboxFarmer.isChecked = true;
                break;
            case BUILDER:
                checkboxBuilder.isChecked = true;
                break;
            case FORESTER:
                checkboxForester.isChecked = true;
                break;
            case ANYONE:
            default:
                checkboxAnyone.isChecked = true;
                break;
        }
    }

    private MinionType getMinionType() {
        if (checkboxAnyone.isChecked) {
            return MinionType.ANYONE;
        } else if (checkboxLaborer.isChecked) {
            return MinionType.LABORER;
        } else if (checkboxFarmer.isChecked) {
            return MinionType.FARMER;
        } else if (checkboxBuilder.isChecked) {
            return MinionType.BUILDER;
        } else if (checkboxForester.isChecked) {
            return MinionType.FORESTER;
        } else {
            return MinionType.ANYONE;
        }
    }

    private void clearCheckboxes() {
        checkboxAnyone.isChecked = false;
        checkboxLaborer.isChecked = false;
        checkboxFarmer.isChecked = false;
        checkboxBuilder.isChecked = false;
        checkboxForester.isChecked = false;
    }

    private void scroll() {
//        float delta = Mouse.getDWheel();
//        if (delta == 0) return;
//
//        if (delta < 0) paramsScrollIndex++;
//        else if (delta > 0) paramsScrollIndex--;
//
//        if (paramsScrollIndex < 0) paramsScrollIndex = 0;
//        else if ((parameters.size() - paramsScrollIndex) <= BOX_PER_PAGE)
//            paramsScrollIndex = parameters.size() - BOX_PER_PAGE;
//
//        paramsScrollProgress = (float)paramsScrollIndex / (float)(parameters.size() - BOX_PER_PAGE);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (int i=0; i<BOX_PER_PAGE; i++) {
            int index = paramsScrollIndex + i;
            if (index >= parameters.size()) break;

            int bx = guiLeft + LIST_X;
            int by = guiTop + LIST_Y + BOX_HEIGHT * i;

            if (mouseX >= bx && mouseX <= bx + BOX_WIDTH && mouseY >= by && mouseY <= by + BOX_HEIGHT) {
                selectedParamIndex = i;
                return;
            }
        }

        if (nameField.mouseClicked(mouseX, mouseY, mouseButton)) {
            return;
        }

        if (checkboxAnyone.onMouseClick(mouseX, mouseY, mouseButton)) {
            setMinionType(MinionType.ANYONE);
            return;
        } else if (checkboxLaborer.onMouseClick(mouseX, mouseY, mouseButton)) {
            setMinionType(MinionType.LABORER);
            return;
        } else if (checkboxFarmer.onMouseClick(mouseX, mouseY, mouseButton)) {
            setMinionType(MinionType.FARMER);
            return;
        } else if (checkboxBuilder.onMouseClick(mouseX, mouseY, mouseButton)) {
            setMinionType(MinionType.BUILDER);
            return;
        } else if (checkboxForester.onMouseClick(mouseX, mouseY, mouseButton)) {
            setMinionType(MinionType.FORESTER);
            return;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
//        if (isScrolling) {
//            int y = mouseY - guiTop - SCROLLBAR_Y;
//            float progress = y / (float)SCROLLBAR_SPACE_H;
//            if (progress < 0) progress = 0;
//            else if (progress > 1) progress = 1;
//
//            scrollIndex = (int)((jobs.length - BOX_PER_PAGE) * progress);
//            scrollProgress = progress;
//        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

//        isScrolling = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (nameField.textboxKeyTyped(typedChar, keyCode))
            return;
    }

    private void playClickSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void onGuiClosed() {
//        Job temp = new Job(job.uuid, nameField.getText(), job.task, getMinionType(), job.priority, job.parameters);
//
//        SUpdateJob packet = new SUpdateJob();
//        packet.job = temp;
//
//        PacketHandler.INSTANCE.sendToServer(packet);
    }
}
