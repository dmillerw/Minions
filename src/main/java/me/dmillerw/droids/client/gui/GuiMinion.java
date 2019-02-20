//package me.dmillerw.droids.client.gui;
//
//import me.dmillerw.droids.client.gui.element.GuiCheckbox;
//import me.dmillerw.droids.client.gui.element.GuiWrappedTextField;
//import me.dmillerw.droids.common.entity.EntityDroid;
//import me.dmillerw.droids.common.ModInfo;
//import me.dmillerw.droids.common.network.PacketHandler;
//import net.minecraft.client.gui.inventory.GuiInventory;
//import net.minecraft.util.ResourceLocation;
//import org.apache.commons.lang3.tuple.Pair;
//import org.lwjgl.input.Keyboard;
//
//import java.io.IOException;
//import java.util.function.Function;
//
//public class GuiMinion extends GuiBase {
//
//    public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation(ModInfo.ID, "textures/gui/minion.png");
//
//    private static final int X_SIZE = 176;
//    private static final int Y_SIZE = 166;
//
//    private EntityDroid minion;
//
//    private MinionType type;
//
//    private GuiWrappedTextField nameField;
//
//    private GuiCheckbox checkboxLaborer;
//    private GuiCheckbox checkboxFarmer;
//    private GuiCheckbox checkboxBuilder;
//    private GuiCheckbox checkboxForester;
//
//    public GuiMinion(EntityDroid minion) {
//        super(INVENTORY_BACKGROUND);
//
//        this.xSize = X_SIZE;
//        this.ySize = Y_SIZE;
//
//        this.minion = minion;
//        this.type = minion.getMinionType();
//    }
//
//    public void initGui() {
//        super.initGui();
//
//        Keyboard.enableRepeatEvents(true);
//
//        this.nameField = new GuiWrappedTextField(this, 63, 20, 105, 20);
//        this.nameField.setFocused(true);
//        this.nameField.setText(this.minion.getSkin());
//
//        final Pair<Integer, Integer> checkboxInactive = Pair.of(0, 166);
//        final Pair<Integer, Integer> checkboxActive = Pair.of(11, 166);
//
//        final Function<GuiCheckbox, Pair<Integer, Integer>> uvMapper = (checkbox) -> checkbox.isChecked ? checkboxActive : checkboxInactive;
//
//        this.checkboxLaborer = new GuiCheckbox(this, 7, 59, 11, 10, "LABORER", 0, false)
//                .setUVMapper(uvMapper).onClick((c) -> setMinionType(MinionType.LABORER));
//
//        this.checkboxFarmer = new GuiCheckbox(this, 7, 74, 11, 10, "FARMER", 0, false)
//                .setUVMapper(uvMapper).onClick((c) -> setMinionType(MinionType.FARMER));
//
//        this.checkboxBuilder = new GuiCheckbox(this, 69, 59, 11, 10, "BUILDER", 0, false)
//                .setUVMapper(uvMapper).onClick((c) -> setMinionType(MinionType.BUILDER));
//
//        this.checkboxForester = new GuiCheckbox(this, 69, 74, 11, 10, "FORESTER", 0, false)
//                .setUVMapper(uvMapper).onClick((c) -> setMinionType(MinionType.FORESTER));
//
//        addElement("field_name", nameField);
//        addElement("checkbox_builder", checkboxBuilder);
//        addElement("checkbox_laborer", checkboxLaborer);
//        addElement("checkbox_farmer", checkboxFarmer);
//        addElement("checkbox_forester", checkboxForester);
//
//        setMinionType(type);
//    }
//
//    @Override
//    protected void drawForeground(int mouseX, int mouseY) {
//        GuiInventory.drawEntityOnScreen(guiLeft + 32, guiTop + 45, 30, (float) (guiLeft + 51) - mouseX, (float) (guiTop + 75 - 50) - mouseY, this.minion);
//        fontRenderer.drawString("Name:", guiLeft + 63, guiTop + 8, 0);
//    }
//
//    @Override
//    protected void keyTyped(char typedChar, int keyCode) throws IOException {
//        if (keyCode == Keyboard.KEY_RETURN) {
//            save();
//            return;
//        }
//
//        super.keyTyped(typedChar, keyCode);
//    }
//
//    private void clearCheckboxes() {
//        checkboxLaborer.isChecked = false;
//        checkboxFarmer.isChecked = false;
//        checkboxBuilder.isChecked = false;
//        checkboxForester.isChecked = false;
//    }
//
//    private void save() {
//        SUpdateMinion packet = new SUpdateMinion();
//        packet.targetUuid = minion.getUniqueID();
//        packet.name = nameField.getText();
//        packet.minionType = type;
//
//        PacketHandler.INSTANCE.sendToServer(packet);
//    }
//
//    private void setMinionType(MinionType type) {
//        this.type = type;
//
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
//            default:
//                break;
//        }
//    }
//
//    @Override
//    public void onGuiClosed() {
//        Keyboard.enableRepeatEvents(false);
//
//        save();
//    }
//}
