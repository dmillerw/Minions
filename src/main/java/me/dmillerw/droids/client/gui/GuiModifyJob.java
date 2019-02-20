//package me.dmillerw.droids.client.gui;
//
//import com.google.common.collect.Lists;
//import me.dmillerw.droids.client.gui.element.*;
//import me.dmillerw.droids.client.gui.modal.GuiModalYesNo;
//import me.dmillerw.droids.client.handler.CoordSelectionHandler;
//import me.dmillerw.droids.common.ModInfo;
//import me.dmillerw.droids.common.network.PacketHandler;
//import me.dmillerw.droids.tasks.Job;
//import me.dmillerw.droids.tasks.TaskDefinition;
//import me.dmillerw.droids.tasks.parameter.Parameter;
//import me.dmillerw.droids.tasks.parameter.ParameterMap;
//import me.dmillerw.droids.common.util.type.Area;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.BlockPos;
//import org.apache.commons.lang3.tuple.Pair;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.UUID;
//import java.util.function.Function;
//
//public class GuiModifyJob extends GuiBase {
//
//    private static final ResourceLocation TEXTURE = new ResourceLocation(ModInfo.ID, "textures/gui/modify_job.png");
//
//    private static final int FONT_COLOR = 0xff544c3b;
//
//    private static final int X_SIZE = 256;
//    private static final int Y_SIZE = 204;
//
//    private static final int SEARCH_X = 29;
//    private static final int SEARCH_Y = 5;
//
//    private static final int LIST_X = 15;
//    private static final int LIST_Y = 18;
//    private static final int LIST_W = 80;
//    private static final int LIST_H = 182;
//
//    private static final int BOX_WIDTH = 80;
//    private static final int BOX_HEIGHT = 13;
//    private static final int BOX_TEXT_X = 2;
//    private static final int BOX_U = 0;
//    private static final int BOX_V = 214;
//
//    private static final int BOX_PER_PAGE = LIST_H / BOX_HEIGHT;
//
//    private static final int PARAM_BOX_X = 99;
//    private static final int PARAM_BOX_Y = 18;
//    private static final int PARAM_BOX_W = 153;
//    private static final int PARAM_BOX_H = 137;
//
//
//    private static final int BUTTON_OK_X = 242;
//    private static final int BUTTON_OK_Y = 191;
//    private static final int BUTTON_OK_W = 11;
//    private static final int BUTTON_OK_H = 10;
//    private static final int BUTTON_OK_U = 7;
//    private static final int BUTTON_OK_V = 204;
//
//    private static final int BUTTON_CANCEL_X = 229;
//    private static final int BUTTON_CANCEL_Y = 191;
//    private static final int BUTTON_CANCEL_W = 11;
//    private static final int BUTTON_CANCEL_H = 10;
//    private static final int BUTTON_CANCEL_U = 18;
//    private static final int BUTTON_CANCEL_V = 204;
//
//    private GuiWrappedTextField nameField;
//
//    private GuiTexturedButton buttonOk;
//    private GuiTexturedButton buttonCancel;
//
//    private GuiCheckbox checkboxAnyone;
//    private GuiCheckbox checkboxLaborer;
//    private GuiCheckbox checkboxFarmer;
//    private GuiCheckbox checkboxBuilder;
//    private GuiCheckbox checkboxForester;
//
//    private Job job;
//
//    private int paramsScrollIndex = 0;
//    private float paramsScrollProgress = 0;
//    private int selectedParamIndex = 0;
//
//    private List<String> parameters = Lists.newArrayList();
//
//    public GuiModifyJob(TaskDefinition task) {
//        this(new Job(UUID.randomUUID(), task.name, task, MinionType.ANYONE, 1, new ParameterMap(task)));
//    }
//
//    public GuiModifyJob(Job job) {
//        super(TEXTURE);
//
//        this.xSize = X_SIZE;
//        this.ySize = Y_SIZE;
//
//        this.job = job;
//
//        parameters.addAll(job.parameters.getKeys());
//    }
//
//    public GuiModifyJob(Job job, String property) {
//        super(TEXTURE);
//
//        this.xSize = X_SIZE;
//        this.ySize = Y_SIZE;
//
//        this.job = job;
//
//        parameters.addAll(job.parameters.getKeys());
//
//        for (int i=0; i<parameters.size(); i++) {
//            if (parameters.get(i).equals(property)) {
//                selectedParamIndex = i;
//            }
//        }
//    }
//
//    public void initGui() {
//        super.initGui();
//
//        this.nameField = new GuiWrappedTextField(this, SEARCH_X, SEARCH_Y, 100, 22)
//                .setFocused(true)
//                .setDrawBackground(false);
//
//        this.buttonOk = new GuiTexturedButton(this, BUTTON_OK_X, BUTTON_OK_Y, BUTTON_OK_W, BUTTON_OK_H)
//                .setTooltip("OK")
//                .setUVMapper((b) -> Pair.of(BUTTON_OK_U, BUTTON_OK_V))
//                .onClick((b) -> {
//                    saveParameters();
//
//                    Job temp = new Job(job.uuid, nameField.getText(), job.task, getMinionType(), job.priority, job.parameters);
//
//                    SUpdateJob packet = new SUpdateJob();
//                    packet.job = temp;
//
//                    PacketHandler.INSTANCE.sendToServer(packet);
//
//                    Navigation.INSTANCE.clear();
//                });
//
//        this.buttonCancel = new GuiTexturedButton(this, BUTTON_CANCEL_X, BUTTON_CANCEL_Y, BUTTON_CANCEL_W, BUTTON_CANCEL_H)
//                .setTooltip("CANCEL")
//                .setUVMapper((b) -> Pair.of(BUTTON_CANCEL_U, BUTTON_CANCEL_V))
//                .onClick((b) -> Navigation.INSTANCE.push(new GuiModalYesNo("Cancel?", "Canceling will erase any changes you may have made", "No", "Yes"), (bool) -> {
//                    if ((Boolean)bool) {
//                        Navigation.INSTANCE.clear();
//                    }
//                }));
//
//        final Pair<Integer, Integer> checkboxInactive = Pair.of(29, 204);
//        final Pair<Integer, Integer> checkboxActive = Pair.of(40, 204);
//
//        final Function<GuiCheckbox, Pair<Integer, Integer>> uvMapper = (checkbox) -> checkbox.isChecked ? checkboxActive : checkboxInactive;
//
//        this.checkboxAnyone = new GuiCheckbox(this, 99, 159, 11, 10, "Anyone", FONT_COLOR, false)
//                .setUVMapper(uvMapper).onClick((c) -> setMinionType(MinionType.ANYONE));
//
//        this.checkboxLaborer = new GuiCheckbox(this, 99, 174, 11, 10, "Laborer", FONT_COLOR, false)
//                .setUVMapper(uvMapper).onClick((c) -> setMinionType(MinionType.LABORER));
//
//        this.checkboxFarmer = new GuiCheckbox(this, 99, 189, 11, 10, "Farmer", FONT_COLOR, false)
//                .setUVMapper(uvMapper).onClick((c) -> setMinionType(MinionType.FARMER));
//
//        this.checkboxBuilder = new GuiCheckbox(this, 159, 159, 11, 10, "Builder", FONT_COLOR, false)
//                .setUVMapper(uvMapper).onClick((c) -> setMinionType(MinionType.BUILDER));
//
//        this.checkboxForester = new GuiCheckbox(this, 159, 174, 11, 10, "Forester", FONT_COLOR, false)
//                .setUVMapper(uvMapper).onClick((c) -> setMinionType(MinionType.FORESTER));
//
//        if (job != null) {
//            nameField.setText(job.title);
//            nameField.setCursorPosition(job.title.length());
//
//            setMinionType(job.type);
//        } else {
//            setMinionType(MinionType.ANYONE);
//        }
//
//        updateParameterProperties();
//    }
//
//    @Override
//    protected void drawForeground(int mouseX, int mouseY) {
//        int x = guiLeft;
//        int y = guiTop;
//
//        for (int i = 0; i < BOX_PER_PAGE; i++) {
//            mc.getTextureManager().bindTexture(TEXTURE);
//
//            GlStateManager.color(1, 1, 1, 1);
//
//            int index = paramsScrollIndex + i;
//            if (index >= parameters.size()) break;
//
//            int bx = x + LIST_X;
//            int by = y + LIST_Y + BOX_HEIGHT * i;
//            String parameter = parameters.get(index);
//
//            if (index == selectedParamIndex) {
//                GlStateManager.color(1, 0, 0, 1);
//                drawTexturedModalRect(bx, by, BOX_U, BOX_V, BOX_WIDTH, BOX_HEIGHT);
//                fontRenderer.drawString(parameter, bx + 5, by + 3, 0xFFFFFFFF);
//            } else {
//                drawTexturedModalRect(bx, by, BOX_U, BOX_V, BOX_WIDTH, BOX_HEIGHT);
//                fontRenderer.drawString(parameter, bx + 5, by + 3, FONT_COLOR);
//            }
//        }
//
//        fontRenderer.drawString("Title", x + 4, y + 5, FONT_COLOR);
//    }
//
//    private void saveParameters() {
//        String key = parameters.get(selectedParamIndex);
//        Parameter parameter = job.parameters.getParameter(key);
//
//        if (parameter.type == Parameter.Type.INT) {
//            GuiWrappedTextField valueField = getElement("param_int_field");
//            job.parameters.setValue(parameter, Integer.parseInt(valueField.getText()));
//        } else if (parameter.type == Parameter.Type.AREA) {
//            GuiWrappedTextField sx = getElement("param_area_field_start_x");
//            GuiWrappedTextField sy = getElement("param_area_field_start_y");
//            GuiWrappedTextField sz = getElement("param_area_field_start_z");
//            GuiWrappedTextField ex = getElement("param_area_field_end_x");
//            GuiWrappedTextField ey = getElement("param_area_field_end_y");
//            GuiWrappedTextField ez = getElement("param_area_field_end_z");
//
//            job.parameters.setValue(parameter, new Area(
//                    new BlockPos(
//                            Integer.parseInt(sx.getText()),
//                            Integer.parseInt(sy.getText()),
//                            Integer.parseInt(sz.getText())
//                    ),
//                    new BlockPos(
//                            Integer.parseInt(ex.getText()),
//                            Integer.parseInt(ey.getText()),
//                            Integer.parseInt(ez.getText())
//                    )));
//        } else if (parameter.type == Parameter.Type.BLOCKPOS) {
//            GuiWrappedTextField x = getElement("param_pos_field_x");
//            GuiWrappedTextField y = getElement("param_pos_field_y");
//            GuiWrappedTextField z = getElement("param_pos_field_z");
//
//            job.parameters.setValue(parameter, new BlockPos(
//                    Integer.parseInt(x.getText()),
//                    Integer.parseInt(y.getText()),
//                    Integer.parseInt(z.getText())
//            ));
//        }
//    }
//
//    private void updateParameterProperties() {
//        clearElements();
//
//        addElement("field_name", nameField);
//        addElement("button_ok", buttonOk);
//        addElement("button_cancel", buttonCancel);
//        addElement("checkbox_anyone", checkboxAnyone);
//        addElement("checkbox_builder", checkboxBuilder);
//        addElement("checkbox_laborer", checkboxLaborer);
//        addElement("checkbox_farmer", checkboxFarmer);
//        addElement("checkbox_forester", checkboxForester);
//
//        String key = parameters.get(selectedParamIndex);
//        Parameter parameter = job.parameters.getParameter(key);
//        Object value = job.parameters.getValue(parameter);
//
//        if (parameter.type == Parameter.Type.INT) {
//            buildIntParam(parameter, (Integer) value);
//        } else if (parameter.type == Parameter.Type.AREA) {
//            buildAreaParam(parameter, (Area) value);
//        } else if (parameter.type == Parameter.Type.BLOCKPOS) {
//            buildBlockPosParam(parameter, (BlockPos) value);
//        }
//    }
//
//    private void buildIntParam(Parameter parameter, int value) {
//        GuiTextLabel label = new GuiTextLabel(this, PARAM_BOX_X + 2, PARAM_BOX_Y + 2, 100, 10)
//                .setLabel("Value");
//        GuiWrappedTextField valueField = new GuiWrappedTextField(this, PARAM_BOX_X + 2, PARAM_BOX_Y + 14, 16, 12)
//                .setText(Integer.toString(value));
//
//        addElement("param_int_label", label);
//        addElement("param_int_field", valueField);
//    }
//
//    private void buildAreaParam(Parameter parameter, Area value) {
//        GuiTextLabel labelStart = new GuiTextLabel(this, PARAM_BOX_X + 2, PARAM_BOX_Y + 2, 100, 10)
//                .setLabel("Area - Start");
//        GuiWrappedTextField fieldStartX = new GuiWrappedTextField(this, PARAM_BOX_X + 3, PARAM_BOX_Y + 14, 30, 12)
//                .setText(Integer.toString(value.startPos.getX()));
//        GuiWrappedTextField fieldStartY = new GuiWrappedTextField(this, PARAM_BOX_X + 37, PARAM_BOX_Y + 14, 30, 12)
//                .setText(Integer.toString(value.startPos.getY()));
//        GuiWrappedTextField fieldStartZ = new GuiWrappedTextField(this, PARAM_BOX_X + 71, PARAM_BOX_Y + 14, 30, 12)
//                .setText(Integer.toString(value.startPos.getZ()));
//        GuiTextLabel labelEnd = new GuiTextLabel(this, PARAM_BOX_X + 2, PARAM_BOX_Y + 30, 100, 10)
//                .setLabel("Area - End");
//        GuiWrappedTextField fieldEndX = new GuiWrappedTextField(this, PARAM_BOX_X + 3, PARAM_BOX_Y + 42, 30, 12)
//                .setText(Integer.toString(value.endPos.getX()));
//        GuiWrappedTextField fieldEndY = new GuiWrappedTextField(this, PARAM_BOX_X + 37, PARAM_BOX_Y + 42, 30, 12)
//                .setText(Integer.toString(value.endPos.getY()));
//        GuiWrappedTextField fieldEndZ = new GuiWrappedTextField(this, PARAM_BOX_X + 71, PARAM_BOX_Y + 42, 30, 12)
//                .setText(Integer.toString(value.endPos.getZ()));
//        GuiWrappedButton buttonPlot = new GuiWrappedButton(this, PARAM_BOX_X + 3, PARAM_BOX_Y + 58, 100, 20, "Plot Point")
//                .onClick((button) -> {
//                    mc.displayGuiScreen(null);
//                    CoordSelectionHandler.INSTANCE.startAreaSelection(job.uuid, (uuid, area) -> {
//                        job.parameters.setValue(parameter, area);
//                        mc.displayGuiScreen(new GuiModifyJob(job, parameter.id));
//                    });
//                });
//
//        addElement("param_area_label_start", labelStart);
//        addElement("param_area_field_start_x", fieldStartX);
//        addElement("param_area_field_start_y", fieldStartY);
//        addElement("param_area_field_start_z", fieldStartZ);
//        addElement("param_area_label_end", labelEnd);
//        addElement("param_area_field_end_x", fieldEndX);
//        addElement("param_area_field_end_y", fieldEndY);
//        addElement("param_area_field_end_z", fieldEndZ);
//        addElement("param_area_button_plot", buttonPlot);
//    }
//
//    private void buildBlockPosParam(Parameter parameter, BlockPos value) {
//        GuiTextLabel label = new GuiTextLabel(this, PARAM_BOX_X + 2, PARAM_BOX_Y + 2, 100, 10)
//                .setLabel("Position");
//        GuiWrappedTextField fieldX = new GuiWrappedTextField(this, PARAM_BOX_X + 3, PARAM_BOX_Y + 14, 30, 12)
//                .setText(Integer.toString(value.getX()));
//        GuiWrappedTextField fieldY = new GuiWrappedTextField(this, PARAM_BOX_X + 37, PARAM_BOX_Y + 14, 30, 12)
//                .setText(Integer.toString(value.getY()));
//        GuiWrappedTextField fieldZ = new GuiWrappedTextField(this, PARAM_BOX_X + 71, PARAM_BOX_Y + 14, 30, 12)
//                .setText(Integer.toString(value.getZ()));
//        GuiWrappedButton buttonPlot = new GuiWrappedButton(this, PARAM_BOX_X + 3, PARAM_BOX_Y + 30, 100, 20, "Plot Point")
//                .onClick((button) -> {
//                    mc.displayGuiScreen(null);
//                    CoordSelectionHandler.INSTANCE.startBlockPosSelection(job.uuid, (uuid, pos) -> {
//                        job.parameters.setValue(parameter, pos);
//                        mc.displayGuiScreen(new GuiModifyJob(job, parameter.id));
//                    });
//                });
//
//        addElement("param_pos_label", label);
//        addElement("param_pos_field_x", fieldX);
//        addElement("param_pos_field_y", fieldY);
//        addElement("param_pos_field_z", fieldZ);
//        addElement("param_pos_button_plot", buttonPlot);
//    }
//
//    private void setMinionType(MinionType type) {
//        clearCheckboxes();
//        switch (type) {
//            case LABORER:
//                checkboxLaborer.isChecked = true;
//                break;
//            case FARMER:
//                checkboxFarmer.isChecked = true;
//                break;
//            case BUILDER:
//                checkboxBuilder.isChecked = true;
//                break;
//            case FORESTER:
//                checkboxForester.isChecked = true;
//                break;
//            case ANYONE:
//            default:
//                checkboxAnyone.isChecked = true;
//                break;
//        }
//    }
//
//    private MinionType getMinionType() {
//        if (checkboxAnyone.isChecked) {
//            return MinionType.ANYONE;
//        } else if (checkboxLaborer.isChecked) {
//            return MinionType.LABORER;
//        } else if (checkboxFarmer.isChecked) {
//            return MinionType.FARMER;
//        } else if (checkboxBuilder.isChecked) {
//            return MinionType.BUILDER;
//        } else if (checkboxForester.isChecked) {
//            return MinionType.FORESTER;
//        } else {
//            return MinionType.ANYONE;
//        }
//    }
//
//    private void clearCheckboxes() {
//        checkboxAnyone.isChecked = false;
//        checkboxLaborer.isChecked = false;
//        checkboxFarmer.isChecked = false;
//        checkboxBuilder.isChecked = false;
//        checkboxForester.isChecked = false;
//    }
//
//    private void scroll() {
////        float delta = Mouse.getDWheel();
////        if (delta == 0) return;
////
////        if (delta < 0) paramsScrollIndex++;
////        else if (delta > 0) paramsScrollIndex--;
////
////        if (paramsScrollIndex < 0) paramsScrollIndex = 0;
////        else if ((parameters.size() - paramsScrollIndex) <= BOX_PER_PAGE)
////            paramsScrollIndex = parameters.size() - BOX_PER_PAGE;
////
////        paramsScrollProgress = (float)paramsScrollIndex / (float)(parameters.size() - BOX_PER_PAGE);
//    }
//
//    @Override
//    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//        for (int i = 0; i < BOX_PER_PAGE; i++) {
//            int index = paramsScrollIndex + i;
//            if (index >= parameters.size()) break;
//
//            int bx = guiLeft + LIST_X;
//            int by = guiTop + LIST_Y + BOX_HEIGHT * i;
//
//            if (mouseX >= bx && mouseX <= bx + BOX_WIDTH && mouseY >= by && mouseY <= by + BOX_HEIGHT) {
//                saveParameters();
//                selectedParamIndex = i;
//                updateParameterProperties();
//                return;
//            }
//        }
//
//        super.mouseClicked(mouseX, mouseY, mouseButton);
//    }
//
//    @Override
//    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
////        if (isScrolling) {
////            int y = mouseY - guiTop - SCROLLBAR_Y;
////            float progress = y / (float)SCROLLBAR_SPACE_H;
////            if (progress < 0) progress = 0;
////            else if (progress > 1) progress = 1;
////
////            scrollIndex = (int)((jobs.length - BOX_PER_PAGE) * progress);
////            scrollProgress = progress;
////        }
//    }
//
//    @Override
//    protected void mouseReleased(int mouseX, int mouseY, int state) {
//        super.mouseReleased(mouseX, mouseY, state);
//
////        isScrolling = false;
//    }
//
//    @Override
//    public void onGuiClosed() {
//
//    }
//}
