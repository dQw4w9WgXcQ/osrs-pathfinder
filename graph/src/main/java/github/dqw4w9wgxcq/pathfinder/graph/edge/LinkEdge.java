package github.dqw4w9wgxcq.pathfinder.graph.edge;

import github.dqw4w9wgxcq.pathfinder.domain.link.Link;

public record LinkEdge(Link link, int cost) implements Edge {
}