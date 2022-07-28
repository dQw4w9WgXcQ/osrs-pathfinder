package github.dqw4w9wgxcq.pathfinder.graphgeneration.componentgraph;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.gridworld.ContiguousComponents;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;
import lombok.AllArgsConstructor;
import net.runelite.cache.region.Position;

@AllArgsConstructor
public class ComponentGraph {
    private final ContiguousComponents components;



    public int componentOf(Position position) {
        return components.map()[position.getZ()][position.getX()][position.getY()];
    }

    public void addLink(Link link) {

    }
}
