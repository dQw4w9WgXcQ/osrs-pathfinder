package github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.underground;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.Edge;
import net.runelite.cache.region.Position;

public record UndergroundEdge(Position destination, int objectId) implements Edge {
}
