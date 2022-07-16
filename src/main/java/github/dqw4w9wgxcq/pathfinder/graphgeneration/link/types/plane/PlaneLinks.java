package github.dqw4w9wgxcq.pathfinder.graphgeneration.link.types.plane;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.definitions.ObjectDefinition;
import net.runelite.cache.region.RegionLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * searches for ladders/stairs and links them to ladders/stairs in plane+-1
 */
@AllArgsConstructor
public class PlaneLinks {
    public static final String UP_ACTION = "Climb-up";
    public static final String DOWN_ACTION = "Climb-down";

    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    public List<Link> findEdges() {
        var regions = regionLoader.getRegions();
        var out = new ArrayList<Link>();
        for (var region : regions) {
            throw new UnsupportedOperationException("Not implemented");//todo
        }

        return out;
    }

    public static boolean isUpObject(ObjectDefinition definition) {
        var action = definition.getActions()[0];
        return action != null && action.equals(UP_ACTION);
    }

    public static boolean isDownObject(ObjectDefinition definition) {
        var action = definition.getActions()[0];
        return action != null && action.equals(DOWN_ACTION);
    }
}
