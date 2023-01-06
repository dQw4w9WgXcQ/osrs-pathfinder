package github.dqw4w9wgxcq.pathfinder.graphgeneration.componentgraph;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Links;
import net.runelite.cache.region.Position;

public record ComponentGraph {
    public static ComponentGraph create(ContiguousComponents components, Links links) {
        throw new Error("not implemented");//todo
    }

    public int componentOf(Position position) {
        return components.map()[position.getZ()][position.getX()][position.getY()];
    }
}
