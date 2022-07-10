package github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.stairs;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.edge.Edge;
import lombok.Getter;
import net.runelite.cache.region.Position;

public class StairEdge extends Edge {
    @Getter
    private final int objectId;

    public StairEdge(Position destination, int objectId) {
        super(destination);
        this.objectId = objectId;
    }
}
