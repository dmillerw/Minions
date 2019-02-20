package me.dmillerw.droids.common.mesh;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

public class AIMeshNetwork {

    private static final int PROCESS_PENDING_PER_TICK = 10;

    private static Map<Integer, Map<UUID, AIMeshNetwork>> worldNetworkMap = Maps.newHashMap();
    private static Map<Integer, Map<BlockPos, AIMeshNetwork>> posToNetworkMap = Maps.newHashMap();

    public static void registerNetwork(World world, AIMeshNetwork network) {
        Map<UUID, AIMeshNetwork> networkMap = worldNetworkMap.get(world.provider.getDimension());
        if (networkMap == null) networkMap = Maps.newHashMap();
        networkMap.put(network.gridId, network);
        worldNetworkMap.put(world.provider.getDimension(), networkMap);
    }

    public static void destroyNetwork(World world, AIMeshNetwork network) {
        network.destroyNetwork();

        Map<UUID, AIMeshNetwork> networkMap = worldNetworkMap.get(world.provider.getDimension());
        if (networkMap == null) networkMap = Maps.newHashMap();
        networkMap.remove(network.gridId, network);
        worldNetworkMap.put(world.provider.getDimension(), networkMap);
    }

    public static AIMeshNetwork getNetwork(INetworkComponent component) {
        Map<UUID, AIMeshNetwork> networkMap = worldNetworkMap.get(component.getWorld().provider.getDimension());
        if (networkMap == null || networkMap.isEmpty())
            return null;

        AIMeshNetwork network = networkMap.values().stream()
                .filter((n) -> n.coverage.contains(component.getPosition()))
                .findFirst().orElse(null);

        return network;
    }

    public static AIMeshNetwork getNetwork(EntityDroid droid) {
        Map<UUID, AIMeshNetwork> networkMap = worldNetworkMap.get(droid.world.provider.getDimension());
        if (networkMap == null || networkMap.isEmpty())
            return null;

        AIMeshNetwork network = networkMap.values().stream()
                .filter((n) -> n.coverage.contains(droid.getPosition()))
                .findFirst().orElse(null);

        return network;
    }

    public static void addToNetwork(INetworkComponent component) {
        AIMeshNetwork network = getNetwork(component);
        if (network != null) {
            network.registerComponent(component);
        }
    }

    public static void removeFromNetwork(INetworkComponent component) {
        AIMeshNetwork network = component.getNetwork();
        if (network != null) {
            network.removeComponent(component);
        }
    }

    public static Collection<AIMeshNetwork> getAllNetworks(World world) {
        Map<UUID, AIMeshNetwork> map = worldNetworkMap.get(world.provider.getDimension());
        if (map != null) return map.values();
        return Collections.EMPTY_LIST;
    }

    public final UUID gridId;
    private final World world;
    private final TileAIController controller;

    private Set<BlockPos> coverage = Sets.newHashSet();

    private Map<BlockPos, INetworkComponent> networkComponents = Maps.newHashMap();
    private Map<BlockPos, IActionProvider> actionProviders = Maps.newHashMap();

    private Map<String, Action> runningActions = Maps.newConcurrentMap();

    public AIMeshNetwork(TileAIController controller) {
        this.gridId = UUID.randomUUID();
        this.world = controller.getWorld();
        this.controller = controller;

        if (!controller.getWorld().isRemote)
            recalculateCoverage();
    }

    public void tick() {
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
                    Action action = provider.getNextAction(droid);
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
        Set<BlockPos> tempCoverage = Sets.newHashSet();
        addCoverage(tempCoverage, controller.getPos(), controller.getRange());
        networkComponents.forEach((pos, provider) -> addCoverage(tempCoverage, pos, provider.getRange()));

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
        networkComponents.values().forEach((component -> component.setNetwork(null)));
    }

    public void updateAction(Action action) {
        if (runningActions.containsKey(action.getKey()))
            runningActions.put(action.getKey(), action);
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
}
