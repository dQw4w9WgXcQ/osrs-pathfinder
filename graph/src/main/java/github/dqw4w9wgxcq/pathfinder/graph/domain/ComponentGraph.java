package github.dqw4w9wgxcq.pathfinder.graph.domain;

import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.graph.edge.LinkEdge;

import java.util.List;
import java.util.Map;

public record ComponentGraph(Map<Link, List<LinkEdge>> graph, List<List<Link>> links) {
    public List<Link> linksOf(int id) {
        return links.get(id);
    }
}
