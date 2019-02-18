package me.dmillerw.minions.tasks.impl.collectitems;

import me.dmillerw.minions.entity.EntityMinion;
import me.dmillerw.minions.entity.ai.AIHelper;
import me.dmillerw.minions.tasks.TaskStep;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class TaskStepCollectItem extends TaskStep {

    private final EntityItem entityItem;
    private final BlockPos dropoffPoint;

    public TaskStepCollectItem(UUID uuid, EntityItem entityItem, BlockPos dropoffPoint) {
        super(uuid);

        this.entityItem = entityItem;
        this.dropoffPoint = dropoffPoint;
    }

    @Override
    public boolean canPerform(EntityMinion minion) {
        return minion.aiHelper.canPathTo(entityItem.getPosition());
    }

    @Override
    public boolean shouldDie(World world) {
        return entityItem == null || entityItem.isDead;
    }

    @Override
    public void tick(EntityMinion minion) {
        AIHelper aiHelper = minion.aiHelper;
        ItemStack heldItem = minion.getHeldItem();

        if (heldItem.isEmpty()) {
            final BlockPos target = new BlockPos(entityItem);
            if (minion.getPosition().equals(target) || minion.getPosition().distanceSq(target) < 1) {
                minion.getNavigator().clearPath();
                minion.setVelocity(0, 0, 0);

                ItemStack item = entityItem.getItem().copy();
                entityItem.setDead();

                minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, item);
            } else {
                aiHelper.moveToEntity(entityItem);
            }
        } else {
            if (minion.getPosition().distanceSq(dropoffPoint) <= 1) {
                minion.getNavigator().clearPath();
                minion.setVelocity(0, 0, 0);

                EntityItem item = new EntityItem(minion.world, minion.posX, minion.posY, minion.posZ, heldItem.copy());
                item.setPosition(dropoffPoint.getX() + 0.5, dropoffPoint.getY() + 0.5, dropoffPoint.getZ() + 0.5);
                item.setVelocity(0, 0, 0);
                minion.world.spawnEntity(item);

                minion.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);

                //TODO implement way to end task gracefully
                minion.activeTaskStep = null;
            } else {
                // Move towards it otherwise
                aiHelper.moveToPosition(dropoffPoint);
            }
        }
    }
}
