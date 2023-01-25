package github.dqw4w9wgxcq.pathfinder.pathfinding.domain;

import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.pathfinding.edge.LinkEdge;

import java.util.List;
import java.util.Map;

public record ComponentGraph(
        Map<Link, List<LinkEdge>> graph,
        List<List<Link>> outboundLinks,
        List<List<Link>> inboundLinks
) {
    public List<Link> linksOfComponent(int id, boolean outbound) {
        return outbound ? outboundLinks.get(id) : inboundLinks.get(id);
    }
}