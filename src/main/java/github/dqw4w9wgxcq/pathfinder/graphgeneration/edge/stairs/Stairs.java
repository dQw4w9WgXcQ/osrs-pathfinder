package github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.stairs;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.Edge;
import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.RegionLoader;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class Stairs {
    public static final String UP_ACTION = "Climb-up";
    public static final String DOWN_ACTION = "Climb-down";

    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    public List<Edge> findEdges() {
        var regions = regionLoader.getRegions();
        var out = new ArrayList<Edge>();
        for (var region : regions) {
            throw new UnsupportedOperationException("Not implemented");//todo
        }

        return out;
    }

    public static boolean isClimbUpObject(ObjectDefinition definition) {
        var action = definition.getActions()[0];
        return action != null && action.equals(UP_ACTION);
    }
}
