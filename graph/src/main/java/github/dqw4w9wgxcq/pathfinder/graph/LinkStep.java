package github.dqw4w9wgxcq.pathfinder.graph;

import github.dqw4w9wgxcq.pathfinder.domain.link.Link;

public record LinkStep(LinkType type, Link link) implements PathStep {
    public LinkStep(Link link) {
        this(LinkType.of(link), link);
    }
}
