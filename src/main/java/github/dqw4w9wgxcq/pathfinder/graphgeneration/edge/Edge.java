package github.dqw4w9wgxcq.pathfinder.graphgeneration.edge;

import lombok.Data;
import net.runelite.cache.region.Position;

@Data
public abstract class Edge {
    private final Position destination;
}
