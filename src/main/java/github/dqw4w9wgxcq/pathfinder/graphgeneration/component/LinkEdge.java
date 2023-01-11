package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import github.dqw4w9wgxcq.pathfinder.graphgeneration.algo.Edge;
import github.dqw4w9wgxcq.pathfinder.graphgeneration.link.Link;

import java.util.List;

public record LinkEdge(Link link, int walkingDistance, List<LinkEdge> adjacent) implements Edge<LinkEdge> {
    public int cost() {
        return link.cost() + walkingDistance;
    }

    public void addAdjacent(LinkEdge linkEdge) {
        adjacent.add(linkEdge);
    }
}
