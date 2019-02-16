package me.dmillerw.minions.client.gui;

import me.dmillerw.minions.client.data.ClientSyncHandler;
import me.dmillerw.minions.client.gui.element.GuiTexturedButton;
import me.dmillerw.minions.client.gui.element.GuiWrappedTextField;
import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.network.server.SUnregisterStateListener;
import me.dmillerw.minions.tasks.Job;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.Arrays;

public class GuiJobList extends GuiBase {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/jobs.png");

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

    private GuiWrappedTextField searchField;

    private GuiTexturedButton addButton;
    private GuiTexturedButton editButton;
    private GuiTexturedButton deleteButton;

    private Job[] jobs;

    private int hoveredJobIndex = -1;

    private int scrollIndex = 0;
    private float scrollProgress = 0;
    private boolean isScrolling = false;

    public GuiJobList() {
        super(TEXTURE);

        this.xSize = X_SIZE;
        this.ySize = Y_SIZE;

        ClientSyncHandler.INSTANCE.requestBatchUpdate();
        ClientSyncHandler.INSTANCE.registerStateListener(this::onUpdate);
    }

    private void onUpdate() {
        this.jobs = ClientSyncHandler.INSTANCE.getJobs();
    }

    public void initGui() {
        super.initGui();

        this.searchField = new GuiWrappedTextField(this, 44, 5, 100, 22)
                .setFocused(true)
                .setDrawBackground(false)
                .onTextChange(this::updateSearch);

        addElement(searchField);

        this.addButton = new GuiTexturedButton(this, ADD_BUTTON_X, ADD_BUTTON_Y, ADD_BUTTON_W, ADD_BUTTON_H)
                .setTooltip("Add Job")
                .setUVMapper((element) -> Pair.of(ADD_BUTTON_U, ADD_BUTTON_V))
                .onClick((element) -> addJob());

        this.editButton = new GuiTexturedButton(this, 0, 0, EDIT_BUTTON_W, EDIT_BUTTON_H)
                .setUVMapper((element) -> Pair.of(EDIT_BUTTON_U, EDIT_BUTTON_V))
                .onClick((element) -> editJob());

        this.deleteButton = new GuiTexturedButton(this, 0, 0, DELETE_BUTTON_W, DELETE_BUTTON_H)
                .setUVMapper((element) -> Pair.of(DELETE_BUTTON_U, DELETE_BUTTON_V))
                .onClick((element) -> deleteJob());

        addElement(addButton);
        addElement(editButton);
        addElement(deleteButton);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        int hoveredIndex = -1;

        drawTexturedModalRect(guiLeft + SCROLLBAR_X, (int)(guiTop + SCROLLBAR_Y + SCROLLBAR_SPACE_H * scrollProgress), SCROLLBAR_U, SCROLLBAR_V, SCROLLBAR_W, SCROLLBAR_H);

        fontRenderer.drawString("Search", guiLeft + 4, guiTop + 5, 0xff544c3b);

        if (jobs != null) {
            if (jobs.length == 0) {
                fontRenderer.drawString("You don't have any jobs defined at the moment", guiLeft + 4, guiTop + 18, 0xff544c3b);
            } else {
                for (int i = 0; i < Math.min(jobs.length, BOX_PER_PAGE); i++) {
                    int index = Math.max(0, scrollIndex + i);
                    if (index >= jobs.length)
                        break;

                    Job job = jobs[index];
                    int bx = guiLeft + 4;
                    int by = guiTop + 18 + BOX_HEIGHT * i;

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    mc.getTextureManager().bindTexture(TEXTURE);

                    drawTexturedModalRect(bx, by, BOX_U, BOX_V, BOX_WIDTH, BOX_HEIGHT);

                    boolean hovered = false;

                    if (mouseX > bx && mouseX < bx + BOX_WIDTH && mouseY > by && mouseY < by + BOX_HEIGHT) {
                        hoveredIndex = i;
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

        hoveredJobIndex = hoveredIndex;
        updateJobButtons(hoveredJobIndex);
    }

    private void updateJobButtons(int i) {
        if (i == -1) {
            editButton.setActive(false).setVisible(false);
            deleteButton.setActive(false).setVisible(false);
        } else {
            int bx = guiLeft + 4;
            int by = guiTop + 18 + BOX_HEIGHT * i;

            editButton.setPosition(bx + EDIT_BUTTON_X, by + EDIT_BUTTON_Y).setActive(true).setVisible(true);
            deleteButton.setPosition(bx + DELETE_BUTTON_X, by + DELETE_BUTTON_Y).setActive(true).setVisible(true);
        }
    }

    @Override
    protected void onMouseScroll(int mouseX, int mouseY, float delta) {
        if (jobs == null) return;
        if (jobs.length <= BOX_PER_PAGE) return;

        if (delta == 0) return;

        if (delta < 0) scrollIndex++;
        else if (delta > 0) scrollIndex--;

        if (scrollIndex < 0) scrollIndex = 0;
        else if ((jobs.length - scrollIndex) <= BOX_PER_PAGE)
            scrollIndex = jobs.length - BOX_PER_PAGE;

        scrollProgress = (float)scrollIndex / (float)(jobs.length - BOX_PER_PAGE);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int startY = guiTop + SCROLLBAR_Y + (int)(SCROLLBAR_H * scrollProgress);
        int endY = startY + SCROLLBAR_H;
        if (mouseX > guiLeft + SCROLLBAR_X && mouseX < guiLeft + SCROLLBAR_X + SCROLLBAR_W && mouseY >= startY && mouseY <= endY) {
            isScrolling = true;
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
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
        isScrolling = false;
    }

    private void updateSearch(String search) {
        if (jobs == null)
            return;

        scrollIndex = 0;

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

    private void editJob() {
        if (hoveredJobIndex < 0 || hoveredJobIndex >= jobs.length)
            return;

        mc.displayGuiScreen(new GuiModifyJob(jobs[hoveredJobIndex]));
    }

    private void deleteJob() {

    }

    @Override
    public void onGuiClosed() {
        PacketHandler.INSTANCE.sendToServer(new SUnregisterStateListener());
    }
}
