package me.dmillerw.minions.tasks.definition;

import com.google.common.collect.Lists;
import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.entity.ai.AIHelper;
import me.dmillerw.minions.tasks.*;
import me.dmillerw.minions.util.Area;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public class TaskCollectItems extends TaskDefinition  {

    public static final Parameter<Area> HARVEST_AREA = Parameter.newParameter("harvest_area", Parameter.AREA);
    public static final Parameter<BlockPos> DROPOFF_POINT = Parameter.newParameter("dropoff_point", Parameter.BLOCKPOS);

    public TaskCollectItems() {
        super(TaskRegistry.COLLECT_ITEMS);
    }

    @Override
    protected ItemStack getIcon() {
        return new ItemStack(Items.STICK);
    }

    @Override
    public TaskInstance createInstance(UUID jobUuid, ParameterMap parameters) {
        return new TaskCollectItems.Instance(jobUuid, parameters);
    }

    public static class Instance extends TaskInstance {

        public final Area area;
        public final BlockPos dropoffPoint;

        private List<EntityItem> foundItems = Lists.newArrayList();

        private EntityItem targetItem;
        private int itemScanDelay = 0;

        public Instance(UUID jobUuid, ParameterMap parameters) {
            super(jobUuid, parameters);

            this.area = parameters.getParameter(HARVEST_AREA);
            this.dropoffPoint = parameters.getParameter(DROPOFF_POINT);
        }

        @Override
        public void tick(EntityMinion minion, AIHelper aiHelper) {
            final ItemStack heldItem = minion.getHeldItem();
            if (targetItem != null) {
                if (targetItem.isDead) {
                    targetItem = null;
                }
            }

            // If we're carrying an item, it's assumed we're dropping it off
            if (!heldItem.isEmpty()) {
                // Are we close to the dropoff point?
                if (dropoffPoint.distanceSq(minion.getPosition()) <= 1) {
                    EntityItem item = new EntityItem(minion.world, minion.posX, minion.posY, minion.posZ, heldItem.copy());
                    minion.world.spawnEntity(item);

                    minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    // Move towards it otherwise
                    minion.getNavigator().tryMoveToXYZ(dropoffPoint.getX(), dropoffPoint.getY(), dropoffPoint.getZ(), 0.8);
                }
            } else {
                // If we have a target, go after it
                if (targetItem != null) {
                    // Can we pick it up?
                    if (targetItem.getDistance(minion) <= 0.75) {
                        minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, targetItem.getItem().copy());
                        targetItem.setDead();
                    } else {
                        minion.getNavigator().tryMoveToEntityLiving(targetItem, 0.8);
                    }
                } else {
                    if (itemScanDelay == 0) {
                        // Update our knowledge of what items are available
                        foundItems = area.getEntities(minion.world, EntityItem.class);
                        if (!foundItems.isEmpty()) {
                            // Lockon
                            for (EntityItem item : foundItems) {
                                if (item.onGround && !item.cannotPickup()) {
                                    targetItem = item;
                                    break;
                                }
                            }
                        } else {
                            // Wait a second before scanning again
                            //TODO Increase?
                            itemScanDelay = 20;
                        }

                        return;
                    }

                    itemScanDelay--;

                    // Otherwise, are we in the harvest area?
                    if (!area.isEntityInsideOf(minion)) {
                        minion.getNavigator().tryMoveToXYZ(area.getCenter().getX(), area.getCenter().getY(), area.getCenter().getZ(), 0.8);
                    }
                }
            }
        }
    }
}
