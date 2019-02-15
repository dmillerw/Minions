package me.dmillerw.minions.client.gui;

import com.google.common.collect.Lists;
import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.network.server.SUnregisterStateListener;
import me.dmillerw.minions.tasks.TaskDefinition;
import me.dmillerw.minions.tasks.TaskRegistry;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GuiSelectTask extends GuiScreen {

    private static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation(ModInfo.ID, "textures/gui/select_task.png");

    private static final int BOX_PER_PAGE = 7;

    private static final int BOX_WIDTH = 137;
    private static final int BOX_HEIGHT = 26;

    private static final int SCROLLBAR_X = 4;
    private static final int SCROLLBAR_Y = 18;
    private static final int SCROLLBAR_BUTTON_W = 7;
    private static final int SCROLLBAR_BUTTON_H = 9;
    private static final int SCROLLBAR_SPACE_H = 182 - SCROLLBAR_BUTTON_H;
    private static final int SCROLLBAR_U = 0;
    private static final int SCROLLBAR_V = 216;

    private static final int BUTTON_OK_X = 142;
    private static final int BUTTON_OK_Y = 203;
    private static final int BUTTON_OK_W = 11;
    private static final int BUTTON_OK_H = 10;
    private static final int BUTTON_OK_INACTUVE_U = 7;
    private static final int BUTTON_OK_INACTUVE_V = 226;
    private static final int BUTTON_OK_ACTIVE_U = 7;
    private static final int BUTTON_OK_ACTIVE_V = 216;

    private static final int BUTTON_CANCEL_X = 128;
    private static final int BUTTON_CANCEL_Y = 203;
    private static final int BUTTON_CANCEL_W = 11;
    private static final int BUTTON_CANCEL_H = 10;
    private static final int BUTTON_CANCEL_U = 18;
    private static final int BUTTON_CANCEL_V = 216;

    private static final int X_SIZE = 156;
    private static final int Y_SIZE = 216;

    private int guiLeft;
    private int guiTop;

    private List<TaskDefinition> tasks = Lists.newArrayList();

    private GuiTextField searchField;

    private int scrollIndex = 0;
    private float scrollProgress = 0;
    private boolean isScrolling = false;

    private int selectedIndex = -1;

    public GuiSelectTask() {
        tasks = TaskRegistry.getAllTasks();
    }

    public void initGui() {
        super.initGui();

        this.guiLeft = (this.width - X_SIZE) / 2;
        this.guiTop = (this.height - Y_SIZE) / 2;

        this.searchField = new GuiTextField(0, fontRenderer, guiLeft + 44, guiTop + 5, 100, 22);
        this.searchField.setFocused(true);
        this.searchField.setEnableBackgroundDrawing(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scroll();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int x = this.guiLeft;
        int y = this.guiTop;
        this.drawTexturedModalRect(x, y, 0, 0, X_SIZE, Y_SIZE);

        drawTexturedModalRect(x + SCROLLBAR_X, (int) (y + SCROLLBAR_Y + SCROLLBAR_SPACE_H * scrollProgress), SCROLLBAR_U, SCROLLBAR_V, SCROLLBAR_BUTTON_W, SCROLLBAR_BUTTON_H);
        drawTexturedModalRect(x + BUTTON_OK_X, y + BUTTON_OK_Y, selectedIndex > -1 ? BUTTON_OK_ACTIVE_U : BUTTON_OK_INACTUVE_U, selectedIndex > -1 ? BUTTON_OK_ACTIVE_V : BUTTON_OK_INACTUVE_V, BUTTON_OK_W, BUTTON_OK_H);
        drawTexturedModalRect(x + BUTTON_CANCEL_X, y + BUTTON_CANCEL_Y, BUTTON_CANCEL_U, BUTTON_CANCEL_V, BUTTON_CANCEL_W, BUTTON_CANCEL_H);

        fontRenderer.drawString("Select a Task", x + 4, y + 205, 0xff544c3b);
        fontRenderer.drawString("Search", x + 4, y + 5, 0xff544c3b);

        for (int i = 0; i < Math.min(tasks.size(), BOX_PER_PAGE); i++) {
            int index = scrollIndex + i;
            if (index >= tasks.size())
                break;

            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);

            int bx = x + 15;
            int by = y + 18 + BOX_HEIGHT * i;
            TaskDefinition task = tasks.get(index);

            if (index == selectedIndex) {
                GlStateManager.color(1, 0, 0, 1);
                drawTexturedModalRect(bx, by, 29, 216, BOX_WIDTH, BOX_HEIGHT);
                fontRenderer.drawString(task.name, bx + 24, by + 4, 0xFFFFFFFF);
            } else {
                drawTexturedModalRect(bx, by, 29, 216, BOX_WIDTH, BOX_HEIGHT);
                fontRenderer.drawString(task.name, bx + 24, by + 4, 0xff544c3b);
            }

            GlStateManager.color(1, 1, 1, 1);

            mc.getRenderItem().renderItemIntoGUI(task.getRenderIcon(), bx + 5, by + 5);
        }

        searchField.drawTextBox();
    }

    private void scroll() {
        if (tasks.size() <= BOX_PER_PAGE) return;

        float delta = Mouse.getDWheel();
        if (delta == 0) return;

        if (delta < 0) scrollIndex++;
        else if (delta > 0) scrollIndex--;

        if (scrollIndex < 0) scrollIndex = 0;
        else if ((tasks.size() - scrollIndex) <= BOX_PER_PAGE)
            scrollIndex = tasks.size() - BOX_PER_PAGE;

        scrollProgress = (float) scrollIndex / (float) (tasks.size() - BOX_PER_PAGE);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int startY = guiTop + SCROLLBAR_Y + (int) (SCROLLBAR_SPACE_H * scrollProgress);
        int endY = startY + SCROLLBAR_BUTTON_H;
        if (mouseX > guiLeft + SCROLLBAR_X && mouseX < guiLeft + SCROLLBAR_X + SCROLLBAR_BUTTON_W && mouseY >= startY && mouseY <= endY) {
            isScrolling = true;
            return;
        }

        if (searchField.mouseClicked(mouseX, mouseY, mouseButton))
            return;
        
        if (mouseX >= guiLeft + BUTTON_CANCEL_X && mouseX <= guiLeft + BUTTON_CANCEL_X + BUTTON_CANCEL_W && mouseY >= guiTop + BUTTON_CANCEL_Y && mouseY <= guiTop + BUTTON_CANCEL_Y + BUTTON_CANCEL_H) {
            mc.displayGuiScreen(new GuiJobList());
            return;
        }

        if (mouseX >= guiLeft + BUTTON_OK_X && mouseX <= guiLeft + BUTTON_OK_X + BUTTON_OK_W && mouseY >= guiTop + BUTTON_OK_Y && mouseY <= guiTop + BUTTON_OK_Y + BUTTON_OK_H) {
            if (selectedIndex >= 0) {
                mc.displayGuiScreen(new GuiModifyJob(tasks.get(selectedIndex)));
                return;
            }
        }

        for (int i = 0; i < BOX_PER_PAGE; i++) {
            int index = scrollIndex + i;
            if (index >= tasks.size())
                break;

            int bx = guiLeft + 15;
            int by = guiTop + 18 + BOX_HEIGHT * i;
            TaskDefinition task = tasks.get(index);

            if (mouseX >= bx && mouseX <= bx + BOX_WIDTH && mouseY >= by && mouseY <= by + BOX_HEIGHT) {
                if (selectedIndex == index) {
                    selectedIndex = -1;
                } else {
                    selectedIndex = index;
                }
                break;
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (isScrolling) {
            int y = mouseY - guiTop - SCROLLBAR_Y;
            float progress = y / (float) SCROLLBAR_SPACE_H;
            if (progress < 0) progress = 0;
            else if (progress > 1) progress = 1;

            scrollIndex = (int) ((tasks.size() - BOX_PER_PAGE) * progress);
            scrollProgress = progress;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);

        isScrolling = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        if (searchField.textboxKeyTyped(typedChar, keyCode)) {
            updateSearch();
        }
    }

    private void updateSearch() {
        scrollIndex = 0;

        String search = searchField.getText().trim();
        if (search.isEmpty()) {
            this.tasks = TaskRegistry.getAllTasks();
        } else {
            this.tasks = TaskRegistry.getAllTasks().stream()
                    .filter((task) -> task.id.contains(search))
                    .collect(Collectors.toList());
        }
    }

    private void playClickSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void onGuiClosed() {
        PacketHandler.INSTANCE.sendToServer(new SUnregisterStateListener());
    }
}
