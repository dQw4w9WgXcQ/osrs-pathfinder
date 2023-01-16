package github.dqw4w9wgxcq.pathfinder.graphgeneration.component;

import github.dqw4w9wgxcq.pathfinder.graph.domain.link.Link;

import java.util.List;

public record LinkedComponent(int id, List<Link> outboundLinks, List<Link> inboundLinks) {
}
