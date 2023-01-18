package github.dqw4w9wgxcq.pathfinder.graph.domain;

import github.dqw4w9wgxcq.pathfinder.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.domain.link.LinkType;

public record LinkStep(LinkType type, Link link) implements PathStep {
    public LinkStep(Link link) {
        this(LinkType.of(link), link);
    }
}
