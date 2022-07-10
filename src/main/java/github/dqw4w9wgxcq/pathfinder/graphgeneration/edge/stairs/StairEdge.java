package github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.stairs;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.Edge;
import net.runelite.cache.region.Position;

public record StairEdge(Position destination, int objectId) implements Edge {
}
