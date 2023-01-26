package github.dqw4w9wgxcq.pathfinder.commons.domain.pathstep;

import github.dqw4w9wgxcq.pathfinder.commons.domain.link.Link;
import github.dqw4w9wgxcq.pathfinder.commons.domain.link.LinkType;

public record LinkStep(LinkType type, Link link) implements PathStep {
    public LinkStep(Link link) {
        this(LinkType.of(link), link);
    }
}
