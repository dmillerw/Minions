package me.dmillerw.minions.client.gui;

import com.google.common.collect.Lists;
import me.dmillerw.minions.client.gui.element.GuiTexturedButton;
import me.dmillerw.minions.client.gui.element.GuiWrappedTextField;
import me.dmillerw.minions.lib.ModInfo;
import me.dmillerw.minions.network.PacketHandler;
import me.dmillerw.minions.network.server.SUnregisterStateListener;
import me.dmillerw.minions.tasks.TaskDefinition;
import me.dmillerw.minions.tasks.TaskRegistry;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class GuiSelectTask extends GuiBase {

    private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/select_task.png");

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

    private List<TaskDefinition> tasks = Lists.newArrayList();

    private GuiWrappedTextField searchField;

    private GuiTexturedButton buttonOk;
    private GuiTexturedButton buttonCancel;

    private int scrollIndex = 0;
    private float scrollProgress = 0;
    private boolean isScrolling = false;

    private int selectedIndex = -1;

    public GuiSelectTask() {
        super(TEXTURE);

        this.xSize = X_SIZE;
        this.ySize = Y_SIZE;

        tasks = TaskRegistry.getAllTasks();
    }

    public void initGui() {
        super.initGui();

        this.searchField = new GuiWrappedTextField(this, 44, 5, 100, 22)
                .setFocused(true)
                .setDrawBackground(false)
                .onTextChange(this::updateSearch);

        addElement("field_search", searchField);

        this.buttonOk = new GuiTexturedButton(this, BUTTON_OK_X, BUTTON_OK_Y, BUTTON_OK_W, BUTTON_OK_H)
                .setUVMapper((b) -> selectedIndex == -1
                        ? Pair.of(BUTTON_OK_INACTUVE_U, BUTTON_OK_INACTUVE_V)
                        : Pair.of(BUTTON_OK_ACTIVE_U, BUTTON_OK_ACTIVE_V))
                .onClick((b) -> {
                    if (selectedIndex >= 0) {
                        Navigation.INSTANCE.push(new GuiModifyJob(tasks.get(selectedIndex)));
                    }
                });

        this.buttonCancel = new GuiTexturedButton(this, BUTTON_CANCEL_X, BUTTON_CANCEL_Y, BUTTON_CANCEL_W, BUTTON_CANCEL_H)
                .setUVMapper((b) -> Pair.of(BUTTON_CANCEL_U, BUTTON_CANCEL_V))
                .onClick((b) -> Navigation.INSTANCE.pop());

        addElement("button_ok", buttonOk);
        addElement("button_cancel", buttonCancel);
    }

    @Override
    protected void drawForeground(int mouseX, int mouseY) {
        int x = guiLeft;
        int y = guiTop;

        drawTexturedModalRect(x + SCROLLBAR_X, (int) (y + SCROLLBAR_Y + SCROLLBAR_SPACE_H * scrollProgress), SCROLLBAR_U, SCROLLBAR_V, SCROLLBAR_BUTTON_W, SCROLLBAR_BUTTON_H);

        fontRenderer.drawString("Select a Task", x + 4, y + 205, 0xff544c3b);
        fontRenderer.drawString("Search", x + 4, y + 5, 0xff544c3b);

        for (int i = 0; i < Math.min(tasks.size(), BOX_PER_PAGE); i++) {
            int index = scrollIndex + i;
            if (index >= tasks.size())
                break;

            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(TEXTURE);

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
    }

    @Override
    protected void onMouseScroll(int mouseX, int mouseY, float delta) {
        if (tasks.size() <= BOX_PER_PAGE) return;

        if (delta == 0) return;

        if (delta < 0) scrollIndex++;
        else if (delta > 0) scrollIndex--;

        if (scrollIndex < 0) scrollIndex = 0;
        else if ((tasks.size() - scrollIndex) <= BOX_PER_PAGE)
            scrollIndex = tasks.size() - BOX_PER_PAGE;

        scrollProgress = (float) scrollIndex / (float) (tasks.size() - BOX_PER_PAGE);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int startY = guiTop + SCROLLBAR_Y + (int) (SCROLLBAR_SPACE_H * scrollProgress);
        int endY = startY + SCROLLBAR_BUTTON_H;
        if (mouseX > guiLeft + SCROLLBAR_X && mouseX < guiLeft + SCROLLBAR_X + SCROLLBAR_BUTTON_W && mouseY >= startY && mouseY <= endY) {
            isScrolling = true;
            return;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);

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

                return;
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

    private void updateSearch(String search) {
        scrollIndex = 0;

        if (search.isEmpty()) {
            this.tasks = TaskRegistry.getAllTasks();
        } else {
            this.tasks = TaskRegistry.getAllTasks().stream()
                    .filter((task) -> task.id.contains(search))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void onGuiClosed() {
        PacketHandler.INSTANCE.sendToServer(new SUnregisterStateListener());
    }
}
