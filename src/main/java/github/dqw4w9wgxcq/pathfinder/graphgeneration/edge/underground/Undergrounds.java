package github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.underground;

import lombok.AllArgsConstructor;
import net.runelite.cache.ObjectManager;
import net.runelite.cache.region.RegionLoader;

import java.util.List;

/**
 * searches for stairs/ladders on plane 0 going down and links them to a stair/ladder in the underground area (100 regions aka 6400 tiles up)
 */
@AllArgsConstructor
public class Undergrounds {
    private final RegionLoader regionLoader;
    private final ObjectManager objectManager;

    public List<UndergroundEdge> findEdges() {
        throw new UnsupportedOperationException("Not implemented");//todo
    }
}
