package me.dmillerw.minions.client.gui;

import me.dmillerw.minions.client.data.ClientSyncHandler;
import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.network.server.SUnregisterStateListener;
import me.dmillerw.minions.tasks.Job;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Arrays;

public class GuiJobList extends GuiScreen {

    private static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation(ModInfo.ID, "textures/gui/jobs.png");

    private static final int X_SIZE = 256;
    private static final int Y_SIZE = 204;

    private static final int BOX_PER_PAGE = 7;

    private static final int BOX_WIDTH = 236;
    private static final int BOX_HEIGHT = 26;
    private static final int BOX_U = 0;
    private static final int BOX_V = 214;

    private static final int ADD_BUTTON_X = 237;
    private static final int ADD_BUTTON_Y = 4;
    private static final int ADD_BUTTON_W = 16;
    private static final int ADD_BUTTON_H = 10;
    private static final int ADD_BUTTON_U = 46;
    private static final int ADD_BUTTON_V = 204;

    private static final int EDIT_BUTTON_W = 19;
    private static final int EDIT_BUTTON_H = 10;
    private static final int EDIT_BUTTON_U = 27;
    private static final int EDIT_BUTTON_V = 204;
    private static final int EDIT_BUTTON_X = BOX_WIDTH - EDIT_BUTTON_W - 2;
    private static final int EDIT_BUTTON_Y = 2;

    private static final int DELETE_BUTTON_W = 27;
    private static final int DELETE_BUTTON_H = 10;
    private static final int DELETE_BUTTON_U = 0;
    private static final int DELETE_BUTTON_V = 204;
    private static final int DELETE_BUTTON_X = BOX_WIDTH - DELETE_BUTTON_W - 2;
    private static final int DELETE_BUTTON_Y = 14;

    private static final int SCROLLBAR_X = 245;
    private static final int SCROLLBAR_Y = 18;
    private static final int SCROLLBAR_W = 7;
    private static final int SCROLLBAR_H = 9;
    private static final int SCROLLBAR_SPACE_H = 182 - SCROLLBAR_H;
    private static final int SCROLLBAR_U = 62;
    private static final int SCROLLBAR_V = 204;

    private int guiLeft;
    private int guiTop;

    private GuiTextField searchField;

    private Job[] jobs;

    private int scrollIndex = 0;
    private float scrollProgress = 0;
    private boolean isScrolling = false;

    public GuiJobList() {
        ClientSyncHandler.INSTANCE.requestBatchUpdate();
        ClientSyncHandler.INSTANCE.registerStateListener(this::onUpdate);
    }

    private void onUpdate() {
        this.jobs = ClientSyncHandler.INSTANCE.getJobs();
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

        drawTexturedModalRect(x + ADD_BUTTON_X, y + ADD_BUTTON_Y, ADD_BUTTON_U, ADD_BUTTON_V, ADD_BUTTON_W, ADD_BUTTON_H);
        drawTexturedModalRect(x + SCROLLBAR_X, (int)(y + SCROLLBAR_Y + SCROLLBAR_SPACE_H * scrollProgress), SCROLLBAR_U, SCROLLBAR_V, SCROLLBAR_W, SCROLLBAR_H);

        fontRenderer.drawString("Search", x + 4, y + 5, 0xff544c3b);

        if (jobs != null) {
            if (jobs.length == 0) {
                fontRenderer.drawString("You don't have any jobs defined at the moment", x + 4, y + 18, 0xff544c3b);
            } else {
                for (int i = 0; i < Math.min(jobs.length, BOX_PER_PAGE); i++) {
                    int index = scrollIndex + i;
                    if (index >= jobs.length)
                        break;

                    Job job = jobs[index];
                    int bx = x + 4;
                    int by = y + 18 + BOX_HEIGHT * i;

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);

                    drawTexturedModalRect(bx, by, BOX_U, BOX_V, BOX_WIDTH, BOX_HEIGHT);

                    boolean hovered = false;

                    if (mouseX > bx && mouseX < bx + BOX_WIDTH && mouseY > by && mouseY < by + BOX_HEIGHT) {
                        hovered = true;

                        drawTexturedModalRect(bx + EDIT_BUTTON_X, by + EDIT_BUTTON_Y, EDIT_BUTTON_U, EDIT_BUTTON_V, EDIT_BUTTON_W, EDIT_BUTTON_H);
                        drawTexturedModalRect(bx + DELETE_BUTTON_X, by + DELETE_BUTTON_Y, DELETE_BUTTON_U, DELETE_BUTTON_V, DELETE_BUTTON_W, DELETE_BUTTON_H);
                    }

                    mc.getRenderItem().renderItemIntoGUI(job.task.getRenderIcon(), bx + 5, by + 5);

                    int width = hovered ? 188 : 216;
                    String title = job.title;
                    String status = "Status: ";
                    String trimmedTitle = fontRenderer.trimStringToWidth(title, width).trim();
                    String trimmedStatus = fontRenderer.trimStringToWidth(status, width).trim();

                    if (trimmedTitle.length() != title.length())
                        trimmedTitle = trimmedTitle + " ...";

                    if (trimmedStatus.length() != status.length())
                        trimmedStatus = trimmedStatus + " ...";

                    fontRenderer.drawString(trimmedTitle, bx + 24, by + 4, 0xff544c3b);
                    fontRenderer.drawString(trimmedStatus, bx + 24, by + 14, 0xff544c3b);
                }
            }
        }

        searchField.drawTextBox();
    }

    private void scroll() {
        if (jobs == null) return;
        if (jobs.length <= BOX_PER_PAGE) return;

        float delta = Mouse.getDWheel();
        if (delta == 0) return;

        if (delta < 0) scrollIndex++;
        else if (delta > 0) scrollIndex--;

        if (scrollIndex < 0) scrollIndex = 0;
        else if ((jobs.length - scrollIndex) <= BOX_PER_PAGE)
            scrollIndex = jobs.length - BOX_PER_PAGE;

        scrollProgress = (float)scrollIndex / (float)(jobs.length - BOX_PER_PAGE);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int startY = guiTop + SCROLLBAR_Y + (int)(SCROLLBAR_H * scrollProgress);
        int endY = startY + SCROLLBAR_H;
        if (mouseX > guiLeft + SCROLLBAR_X && mouseX < guiLeft + SCROLLBAR_X + SCROLLBAR_W && mouseY >= startY && mouseY <= endY) {
            isScrolling = true;
            return;
        }

        if (searchField.mouseClicked(mouseX, mouseY, mouseButton))
            return;

        if (mouseX >= guiLeft + ADD_BUTTON_X && mouseX <= guiLeft + ADD_BUTTON_X + ADD_BUTTON_W && mouseY >= guiTop + ADD_BUTTON_Y && mouseY <= guiLeft + ADD_BUTTON_Y + ADD_BUTTON_H) {
            addJob();
        } else {
            for (int i = 0; i < BOX_PER_PAGE; i++) {
                int index = scrollIndex + i;
                if (index >= jobs.length)
                    break;

                Job job = jobs[index];
                int bx = guiLeft + 4;
                int by = guiTop + 18 + BOX_HEIGHT * i;

                if (mouseX >= bx + EDIT_BUTTON_X && mouseX <= bx + EDIT_BUTTON_X + EDIT_BUTTON_W && mouseY >= by + EDIT_BUTTON_Y && mouseY <= by + EDIT_BUTTON_Y + EDIT_BUTTON_H) {
                    editJob(job);
                } else if (mouseX >= bx + DELETE_BUTTON_X && mouseX <= bx + DELETE_BUTTON_X + DELETE_BUTTON_W && mouseY >= by + DELETE_BUTTON_Y && mouseY <= by + DELETE_BUTTON_Y + DELETE_BUTTON_H) {
                    deleteJob(job);
                }
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (isScrolling) {
            int y = mouseY - guiTop - SCROLLBAR_Y;
            float progress = y / (float)SCROLLBAR_SPACE_H;
            if (progress < 0) progress = 0;
            else if (progress > 1) progress = 1;

            scrollIndex = (int)((jobs.length - BOX_PER_PAGE) * progress);
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
        if (jobs == null)
            return;

        scrollIndex = 0;

        String search = searchField.getText().trim();
        if (search.isEmpty()) {
            this.jobs = ClientSyncHandler.INSTANCE.getJobs();
        } else {
            this.jobs = Arrays.stream(ClientSyncHandler.INSTANCE.getJobs())
                    .filter((job) -> job.title.contains(search))
                    .toArray(Job[]::new);
        }
    }

    private void addJob() {
        mc.displayGuiScreen(new GuiSelectTask());
    }

    private void editJob(Job job) {
        mc.displayGuiScreen(new GuiModifyJob(job));
    }

    private void deleteJob(Job job) {

    }

    private void playClickSound() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    @Override
    public void onGuiClosed() {
        PacketHandler.INSTANCE.sendToServer(new SUnregisterStateListener());
    }
}
