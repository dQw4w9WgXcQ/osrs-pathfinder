package dev.dqw4w9wgxcq.pathfinder.commons.domain.pathfinding;

import dev.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;

import java.util.List;
import java.util.Map;

public record ComponentGraph(
        Map<Link, List<LinkEdge>> graph, List<List<Link>> outboundLinks, List<List<Link>> inboundLinks) {
    public List<Link> linksOfComponent(int id, boolean outbound) {
        return outbound ? outboundLinks.get(id) : inboundLinks.get(id);
    }
}