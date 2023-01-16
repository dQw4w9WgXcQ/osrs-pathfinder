package github.dqw4w9wgxcq.pathfinder.graph.domain;

import github.dqw4w9wgxcq.pathfinder.graph.domain.link.Link;

public record LinkRef(LinkType type, int id) {
    public static LinkRef from(Link link) {
        return new LinkRef(LinkType.of(link), link.id());
    }
}
