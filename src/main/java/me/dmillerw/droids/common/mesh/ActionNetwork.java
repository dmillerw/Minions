package me.dmillerw.droids.common.mesh;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.dmillerw.droids.api.ClaimedObjects;
import me.dmillerw.droids.api.IActionProvider;
import me.dmillerw.droids.api.INetworkComponent;
import me.dmillerw.droids.api.action.Action;
import me.dmillerw.droids.api.action.ActionState;
import me.dmillerw.droids.common.entity.EntityDroid;
import me.dmillerw.droids.common.helper.DroidTracker;
import me.dmillerw.droids.common.tile.TileAIController;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class ActionNetwork {

    private static final int PROCESS_PENDING_PER_TICK = 10;

    private static Map<Integer, Map<UUID, ActionNetwork>> worldNetworkMap = Maps.newHashMap();
    private static Map<Integer, Map<BlockPos, ActionNetwork>> posToNetworkMap = Maps.newHashMap();

    public static void registerNetwork(World world, ActionNetwork network) {
        Map<UUID, ActionNetwork> networkMap = worldNetworkMap.get(world.provider.getDimension());
        if (networkMap == null) networkMap = Maps.newHashMap();
        networkMap.put(network.gridId, network);
        worldNetworkMap.put(world.provider.getDimension(), networkMap);
    }

    public static void destroyNetwork(World world, ActionNetwork network) {
        network.destroyNetwork();

        Map<UUID, ActionNetwork> networkMap = worldNetworkMap.get(world.provider.getDimension());
        if (networkMap == null) networkMap = Maps.newHashMap();
        networkMap.remove(network.gridId, network);
        worldNetworkMap.put(world.provider.getDimension(), networkMap);
    }

    public static ActionNetwork getNetwork(INetworkComponent component) {
        Map<BlockPos, ActionNetwork> networkMap = posToNetworkMap.get(component.getWorld().provider.getDimension());
        if (networkMap == null || networkMap.isEmpty())
            return null;

        return networkMap.get(component.getPosition());
    }

    public static ActionNetwork getNetwork(EntityDroid droid) {
        Map<BlockPos, ActionNetwork> networkMap = posToNetworkMap.get(droid.getPosition());
        if (networkMap == null || networkMap.isEmpty())
            return null;

        return networkMap.get(droid.getPosition());
    }

    public static void addToNetwork(INetworkComponent component) {
        ActionNetwork network = getNetwork(component);
        if (network != null) {
            network.registerComponent(component);
        }
    }

    public static void removeFromNetwork(INetworkComponent component) {
        ActionNetwork network = component.getNetwork();
        if (network != null) {
            network.removeComponent(component);
        }
    }

    public static Collection<ActionNetwork> getAllNetworks(World world) {
        Map<UUID, ActionNetwork> map = worldNetworkMap.get(world.provider.getDimension());
        if (map != null) return map.values();
        return Collections.EMPTY_LIST;
    }

    public final UUID gridId;
    private final World world;
    private final TileAIController controller;
    private final ClaimedObjects claimedObjects;

    private Set<BlockPos> coverage = Sets.newHashSet();

    private Map<BlockPos, INetworkComponent> networkComponents = Maps.newHashMap();
    private Map<BlockPos, IActionProvider> actionProviders = Maps.newHashMap();

    private Map<String, Action> runningActions = Maps.newConcurrentMap();

    private boolean errored = false;

    public ActionNetwork(TileAIController controller) {
        this.gridId = UUID.randomUUID();
        this.world = controller.getWorld();
        this.controller = controller;
        this.claimedObjects = new ClaimedObjects(world);

        if (!controller.getWorld().isRemote)
            recalculateCoverage();
    }

    public void tick() {
        if (errored)
            return;

        Map<String, Action> temp = Maps.newHashMap();

        for (Action action : runningActions.values()) {
            if (action.getProgress() == ActionState.Progress.IN_PROGRESS) {
                temp.put(action.getKey(), action);
            } else {
                action.getDroid().setAction(null);
            }
        }

        runningActions.clear();
        runningActions.putAll(temp);

        for (EntityDroid droid : DroidTracker.getAvailableDroids(world)) {
            for (IActionProvider provider : actionProviders.values()) {
                if (provider.hasAvailableActions()) {
                    Action action = provider.getNextAction(claimedObjects, droid);
                    if (action == null)
                        continue;

                    if (runningActions.containsKey(action.getKey()))
                        continue;

                    runningActions.put(action.getKey(), action);
                    action.setProgress(ActionState.Progress.IN_PROGRESS);
                    action.setDroid(droid);
                    droid.setAction(action);

                    break;
                }
            }
        }
    }

    private boolean canDroidPerformAction(EntityDroid droid, Action action) {
//        List<Requirement> requirements = action.getRequirements();
//        if (requirements == null)
//            return true;
//
//        for (Requirement requirement : requirements) {
//            if (!requirement.canPerform(droid))
//                return false;
//        }
        return true;
    }

    private void recalculateCoverage() {
        Map<BlockPos, ActionNetwork> worldPosMap = posToNetworkMap.get(world.provider.getDimension());
        if (worldPosMap == null) worldPosMap = Maps.newHashMap();

        // Remove our current provided coverage from the global network map
        if (!errored)
            coverage.forEach(worldPosMap::remove);

        // Build up our new coverage map
        Set<BlockPos> tempCoverage = Sets.newHashSet();
        addCoverage(tempCoverage, controller.getPos(), controller.getRange());
        networkComponents.forEach((pos, provider) -> addCoverage(tempCoverage, pos, provider.getRange()));

        // If any blocks within our coverage already exist within the map, mark the network as errored, conflict
        boolean conflict = tempCoverage.stream().anyMatch(worldPosMap::containsKey);

        if (conflict) {
            errored = true;
            destroyNetwork();
            return;
        }

        Map<BlockPos, INetworkComponent> componentMap = Maps.newHashMap();
        networkComponents.forEach((pos, provider) -> {
            if (tempCoverage.contains(pos)) {
                componentMap.put(pos, provider);

                if (provider instanceof IActionProvider)
                    actionProviders.put(pos, (IActionProvider) provider);
            } else {
                provider.setNetwork(null);
            }
        });

        coverage.clear();
        coverage.addAll(tempCoverage);

        Map<BlockPos, ActionNetwork> finalNetworkMap = worldPosMap;
        coverage.forEach((pos) -> finalNetworkMap.put(pos, this));
        posToNetworkMap.put(world.provider.getDimension(), worldPosMap);

        networkComponents.clear();

        for (BlockPos pos : coverage) {
            if (componentMap.containsKey(pos))
                continue;

            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof INetworkComponent && !(tile instanceof TileAIController) && !tile.isInvalid()) {
                componentMap.put(((INetworkComponent) tile).getPosition(), (INetworkComponent) tile);
                ((INetworkComponent) tile).setNetwork(this);
            }
        }

        networkComponents.putAll(componentMap);

        actionProviders.clear();
        componentMap.values().stream()
                .filter((component) -> component instanceof IActionProvider)
                .map((component) -> (IActionProvider) component)
                .forEach((provider) -> actionProviders.put(provider.getPosition(), provider));

        System.out.println("NETWORK: " + gridId);
        System.out.println("COVERAGE: " + coverage.size() + " BLOCKS");
        System.out.println("COMPONENTS: " + networkComponents.size());
        System.out.println("PROVIDERS: " + actionProviders.size());
    }

    public void destroyNetwork() {
        Map<BlockPos, ActionNetwork> networkMap = posToNetworkMap.get(world.provider.getDimension());
        coverage.forEach((pos) -> networkMap.remove(pos));

        coverage.clear();
        networkComponents.values().forEach((component -> component.setNetwork(null)));
        actionProviders.clear();
        runningActions.clear();
    }

    public void updateAction(Action action) {
        if (runningActions.containsKey(action.getKey())) {
            runningActions.put(action.getKey(), action);

            IActionProvider provider = action.getActionProvider();
            if (provider != null) provider.onActionUpdate(claimedObjects, action);
        }
    }

    private void addCoverage(Set<BlockPos> coverage, BlockPos position, int range) {
        if (range <= 0)
            return;

        BlockPos.getAllInBox(
                position.add(-range, Math.min(0, -range), -range),
                position.add(range, Math.min(255, range), range))
                .forEach(coverage::add);
    }

    public TileAIController getController() {
        return controller;
    }

    public Collection<IActionProvider> getActionProviders() {
        return actionProviders.values();
    }

    private void registerComponent(INetworkComponent component) {
        if (networkComponents.containsKey(component.getPosition())) {
            System.err.println("Network component tried to register itself twice: " + component.getPosition());
            return;
        }

        networkComponents.put(component.getPosition(), component);
        if (component instanceof IActionProvider)
            actionProviders.put(component.getPosition(), (IActionProvider) component);

        System.out.println("Adding " + component.getPosition() + " to network: " + gridId);

        recalculateCoverage();

        component.setNetwork(this);
    }

    private void removeComponent(INetworkComponent component) {
        component.setNetwork(null);

        networkComponents.remove(component.getPosition());
        if (component instanceof IActionProvider)
            actionProviders.remove(component.getPosition());

        System.out.println("Removing " + component.getPosition() + " from network: " + gridId);

        recalculateCoverage();

        networkComponents.remove(component.getPosition());
    }

    @Override
    public String toString() {
        return "{id: " + gridId + ", errored: " + errored + ", components: " + networkComponents.size() + ", providers: " + actionProviders.size() + ", coverage: " + coverage.size() + ", runningActions: " + runningActions.size() + "}";
    }
}
